/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gatos;

import java.util.LinkedList;

/**
 * Clase para representar un estado del juego del gato. 
 * Cada estado sabe cÃ³mo generar a sus sucesores.
 * @author Vero
 */
public class Gato {
    
    public static final int MARCA1 = 1;             // NÃºmero usado en el tablero del gato para marcar al primer jugador.
    public static final int MARCA2 = 4;             // Se usan int en lugar de short porque coincide con el tamaÃ±o de la palabra, el cÃ³digo se ejecuta ligeramente mÃ¡s rÃ¡pido.
    
    int[][] tablero = new int[3][3];     // Tablero del juego
    Gato padre;                          // QuiÃ©n generÃ³ este estado.
    LinkedList<Gato> sucesores;          // Posibles jugadas desde este estado.
    boolean jugador1 = false;            // Jugador que tirÃ³ en este tablero.
    boolean hayGanador = false;          // Indica si la Ãºltima tirada produjo un ganador.
    int tiradas = 0;                     // NÃºmero de casillas ocupadas.
  
    /** Constructor del estado inicial. */
    Gato() {}

    /** Constructor que copia el tablero de otro gato y el nÃºmero de tiradas */
    Gato(Gato g){
      for(int i = 0; i < 3; i++){
        for(int j = 0; j < 3; j++){
          tablero[i][j] = g.tablero[i][j];
        }
      }
      tiradas = g.tiradas;
    }

    /** Indica si este estado tiene sucesores expandidos. */
    int getNumHijos(){
      if(sucesores != null) return sucesores.size();
      else return 0;
    }

    /* FunciÃ³n auxiliar.
     * Dada la Ãºltima posiciÃ³n en la que se tirÃ³ y la marca del jugador
     * calcula si esta jugada produjo un ganador y actualiza el atributo correspondiente.
     * 
     * Esta funciÃ³n debe ser lo mÃ¡s eficiente posible para que la generaciÃ³n del Ã¡rbol no sea demasiado lenta.
     */
    void hayGanador(int x, int y, int marca){
      // Horizontal
      if (tablero[y][(x + 1) % 3] == marca && tablero[y][(x + 2) % 3] == marca) { hayGanador = true; return; }
      // Vertical
      if (tablero[(y + 1) % 3][x] == marca && tablero[(y + 2) % 3][x] == marca) { hayGanador = true; return; }
      // Diagonal
      if((x == 1 && y != 1) || (y == 1 && x!= 1)) return; // No pueden hacer diagonal
      // Centro y esquinas
      if(x == 1 && y == 1){
        // Diagonal \
        if(tablero[0][0] == marca && tablero[2][2] == marca) { hayGanador = true; return; }
        if(tablero[2][0] == marca && tablero[0][2] == marca) { hayGanador = true; return; }
      } else if (x == y){
        // Diagonal \
        if (tablero[(y + 1) % 3][(x + 1) % 3] == marca && tablero[(y + 2) % 3][(x + 2) % 3] == marca) { hayGanador = true; return; }
      } else {
        // Diagonal /
        if (tablero[(y + 2) % 3][(x + 1) % 3] == marca && tablero[(y + 1) % 3][(x + 2) % 3] == marca) { hayGanador = true; return; }
      }
    }

    /* FunciÃ³n auxiliar.
     * Coloca la marca del jugador en turno para este estado en las coordenadas indicadas.
     * Asume que la casilla estÃ¡ libre.
     * Coloca la marca correspondiente, verifica y asigna la variable si hay un ganador.
     */
    void tiraEn(int x, int y){
      tiradas++;
      int marca = (jugador1) ? MARCA1 : MARCA2;
      tablero[y][x] = marca;
      hayGanador(x,y, marca);
    }

    /** ------- *** ------- *** -------
     * Este es el mÃ©todo que se deberÃ­a dejar como prÃ¡ctica.
     * ------- *** ------- *** -------
     * Crea la lista sucesores y agrega a todos los estados que sujen de tiradas vÃ¡lidas.
     * Se consideran tiradas vÃ¡lidas a aquellas en una casilla libre.
     * AdemÃ¡s, se optimiza el proceso no agregando estados con jugadas simÃ©tricas.
     * Los estados nuevos tendrÃ¡n una tirada mÃ¡s y el jugador en turno serÃ¡ el jugador contrario.
     */
    LinkedList<Gato> generaSucesores(){
            sucesores = new LinkedList();
            
	    if(hayGanador){
                return null;
            }else{
                for(int i=0; i < 3;i++){
                    for(int j=0; j < 3;j++){
                        Gato nuevo = new Gato(this);
                        if(nuevo.tablero[i][j] == 0){
                            nuevo.padre = this;
                            nuevo.jugador1 = this.jugador1;
                            nuevo.tiraEn(j, i);
                            if(nuevo.jugador1) nuevo.jugador1 = false;
                            else nuevo.jugador1 = true;
                            boolean existe = false;
                            for(Gato g: sucesores){
                                if(g.equals(nuevo)){
                                    existe = true;
                                    break;
                                }			
                            }
                            if(!existe) sucesores.add(nuevo);
                            
                        }
                    }
                }
            }
            return sucesores;
    }


    // ------- *** ------- *** -------
    // Serie de funciones que revisan la equivalencia de estados considerando las simetrÃ­as de un cuadrado.
    // ------- *** ------- *** -------
    // http://en.wikipedia.org/wiki/Examples_of_groups#The_symmetry_group_of_a_square_-_dihedral_group_of_order_8
    // ba es reflexion sobre / y ba3 reflexion sobre \.

    /** Revisa si ambos gatos son exactamente el mismo. */
    boolean esIgual(Gato otro){
      for(int i = 0; i < 3; i++){
        for(int j = 0; j < 3; j++){
          if(tablero[i][j] != otro.tablero[i][j]) return false;
        }
      }
      return true;
    }

    /** Al reflejar el gato sobre la diagonal \ son iguales (ie traspuesta) */
    boolean esSimetricoDiagonalInvertida(Gato otro){            
       for(int i=0; i < 3; i++){
           for(int j=0; j<3; j++){
               if(tablero[i][j] != otro.tablero[j][i]) return false;
           }
       }
        return true;
    }

    /** Al reflejar el gato sobre la diagonal / son iguales (ie traspuesta) */
    boolean esSimetricoDiagonal(Gato otro){
        if(tablero[0][0] != otro.tablero[2][2]) return false;
        if(tablero[0][1] != otro.tablero[2][1]) return false;
        if(tablero[0][2] != otro.tablero[2][0]) return false;
        if(tablero[1][0] != otro.tablero[1][2]) return false;
        if(tablero[1][1] != otro.tablero[1][1]) return false;
        if(tablero[1][2] != otro.tablero[1][0]) return false;
        if(tablero[2][0] != otro.tablero[0][2]) return false;
        if(tablero[2][1] != otro.tablero[0][1]) return false;
        if(tablero[2][2] != otro.tablero[0][0]) return false;
        
        return true;
    }

    /** Al reflejar el otro gato sobre la vertical son iguales */
    boolean esSimetricoVerticalmente(Gato otro){
      for(int i=0; i < 3 ;i++){
            if(tablero[i][0] != otro.tablero[i][2]){
                return false;
            }else if(tablero[i][1] != otro.tablero[i][1]){
                return false;
            }else if(tablero[i][2] != otro.tablero[i][0]){
                return false;
            }
        }  
      
      return true;
    }

    /** Al reflejar el otro gato sobre la horizontal son iguales */
    boolean esSimetricoHorizontalmente(Gato otro){
         for(int i=0; i < 3 ;i++){
            if(tablero[0][i] != otro.tablero[2][i]){
                return false;
            }else if(tablero[1][i] != otro.tablero[1][i]){
                return false;
            }else if(tablero[2][i] != otro.tablero[0][i]){
                return false;
            }
        }  
      return true;
    }

    /** Rota el otro tablero 90Â° en la direcciÃ³n de las manecillas del reloj. */
    boolean esSimetrico90(Gato otro){
      int[][] nuevo = new int[3][3];
      for(int i=0; i < 3 ;i++){
          for(int j=0; j < 3;j++){
              nuevo[j][2-i] = otro.tablero[i][j];
          }
      }
      Gato v2 = new Gato(otro);
      v2.tablero = nuevo;
    
      return this.esIgual(v2);
    }

    /** Rota el otro tablero 180Â° en la direcciÃ³n de las manecillas del reloj. */
    boolean esSimetrico180(Gato otro){
      int[][] nuevo = new int[3][3];
      for(int i = 0; i < 3; i++){
	for(int j = 0; j < 3; j++){
            nuevo[2-i][2-j] = otro.tablero[i][j];
            }
	 }
      Gato v2 = new Gato(otro);
      v2.tablero = nuevo;
            
      return this.esIgual(otro);
    }

    /** Rota el otro tablero 270Â° en la direcciÃ³n de las manecillas del reloj. */
    boolean esSimetrico270(Gato otro){
        int[][] nuevo = new int[3][3];
        for(int i=0; i < 3;i++){
            for(int j=0; j < 3;j++){
                nuevo[2-j][i] = otro.tablero[i][j];
            }
        }
        Gato v2 = new Gato(otro);
        v2.tablero = nuevo;
        
        return this.esIgual(v2);
    }

    /**
     * Indica si dos estados del juego del gato son iguales, considerando simetrÃ­as, 
     * de este modo el problema se vuelve manejable.
     */
    @Override
    public boolean equals(Object o){
        Gato otro = (Gato)o;
        if(esIgual(otro)) return true;

        if(esSimetricoDiagonalInvertida(otro)) return true;
        if(esSimetricoDiagonal(otro)) return true;
        if(esSimetricoVerticalmente(otro)) return true;
        if(esSimetricoHorizontalmente(otro)) return true;
        if(esSimetrico90(otro)) return true;
        if(esSimetrico180(otro)) return true;
        if(esSimetrico270(otro)) return true; // No redujo el diÃ¡metro mÃ¡ximo al agregarlo

        return false;
    }

    /** Devuelve una representaciÃ³n con caracteres de este estado.
     *  Se puede usar como auxiliar al probar segmentos del cÃ³digo. 
     */
    @Override
    public String toString(){
        char simbolo = jugador1 ? 'o' : 'x';
        String gs = "";
        for(int i = 0; i < 3; i++){
          for(int j = 0; j < 3; j++){
            gs += tablero[i][j] + " ";
          }
          gs += '\n';
        }
        return gs;
    }
}
