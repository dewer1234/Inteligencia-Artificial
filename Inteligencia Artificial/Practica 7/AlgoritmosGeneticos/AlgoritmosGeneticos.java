import java.util.*;

public class AlgoritmosGeneticos{

	static class Tablero{
	   
	    private int corte;
	    private int[] tablero;
	    private double aptitud;
	    private Random random;
	    private int tam;

	    //Constructor de la clase Tablero
	    public Tablero(int tam2){
			this.tam = tam2;
			this.random = new Random();
			this.tablero = new int[this.tam];
			if(tam <= 1){
			    this.corte = 0;
			}
			else{
			    this.corte = random.nextInt(this.tam-1)+1;
			}
			for(int i = 0; i < this.tam; i++){
			    this.tablero[i] = random.nextInt(this.tam);
			}
			this.aptitud = calculaAptitud();
		}

	    //Metodo que nos regresa el tamaño del tablero
	    public int getTam(){
			return this.tam;
	    }

	    //Metodo que nos regresa la Aptitud
	   	public double getAptitud(){
			return this.aptitud;
	    }

	    //Metodo que nos regresa la reyna en una columna definida
	    public int getReyna(int pos){
			return this.tablero[pos];
	    }

	    //Metodo que calcula la funcion de aptitud
	    public double calculaAptitud(){
			int coincidencias = 0;
			int diag = 1;
			for(int i = 0; i < this.tam; i++){	    
			    for(int j = i+1; j < this.tam; j++){

					if(this.tablero[j] == this.tablero[i]){
					    coincidencias++;	    	   
					}
					if(this.tablero[j]+diag == this.tablero[i]){
					    coincidencias++;
					}
					if(this.tablero[j]-diag == this.tablero[i]){
					    coincidencias++;
					}
					diag++;
			    }
			    diag = 1;
			}
			return coincidencias;
	    }



	    //Metodo que asigna un nuevo tablero a un nuevo objeto
	    public void setTablero(int[] tablero){	
			this.tam = tablero.length;
			this.corte = random.nextInt(this.tam-1)+1;
			this.tablero = tablero;
			this.aptitud = calculaAptitud();
	    }


	    //Metodo para mutar los valores del tablero
	   public void muta(){
			for(int i = 0; i < this.tam; i++){
			    if(this.random.nextDouble() <= 0.2){
					this.tablero[i] = this.random.nextInt(this.tam-1);
			    }
			}
    	}


	    //Metodo para recombinar tableros
	    public Tablero recombina(Tablero t){
			Tablero aux = new Tablero(0);
			int[] tableroNuevo = new int[this.tam]; 
			if(t.tam != this.tam)
			    throw new IndexOutOfBoundsException("Error: los tableros tienen tamaño diferentes");
			for(int i = 0; i < this.tam; i++){
			    if(i <= this.corte){
					tableroNuevo[i] = this.tablero[i];
			    }else{
					tableroNuevo[i] = t.getReyna(i);
			    }
			}
			aux.setTablero(tableroNuevo);
			return aux;
	    }



	}

	static class Poblacion{
	    private Set<Tablero> individuos;
	    private int tam;
	    private int tamanioTablero;
	    private Tablero elitismo;
	    private Random ruleta;

	    //Constructor de la clase Poblacion
	    public Poblacion(int muestra, int sizeTablero){
			this.ruleta = new Random();
			this.tam = muestra;
			this.tamanioTablero = sizeTablero;
			this.individuos = new HashSet<Tablero>();
			for(int i = 0; i < this.tam; i++){
			    Tablero aux = new Tablero(this.tamanioTablero);
			    if(this.elitismo == null || this.elitismo.getAptitud() >= aux.getAptitud()){
					elitismo = aux;
				}
			    this.individuos.add(aux);
			}
	    }
	    
	    //Constructor de la clase Poblacion cuando se recibe un tamaño de un conjunto de estudio
	    public Poblacion(int tamConjunto){
			this(tamConjunto,8);
	    }

	    //Metodo para colocar un tablero con mas preferencia sobre los demas al conjunto
	   	public void setElitismo(Tablero t){
			this.elitismo = t;
	    }    

	    //Metodo para obtener el tablero con preferencia
	    public Tablero getElitismo(){
			return this.elitismo;
	    } 

	    //Metodo que nos regresa todos los tableros de la poblacion
	    public Set<Tablero> getIndividuos(){
			return this.individuos;
	    }

	    //Metodo que nos regresa el tamaño de la poblacion estudiada
	    public int getTam(){
			return this.tam;
	    }

	    //Metodo para eliminar un objeto de una muestra
	    public Tablero eliminaAleatorio(){
			int aleatorio = this.ruleta.nextInt(this.individuos.size());
			Iterator it = this.individuos.iterator();
			for(int i = 0; i < aleatorio; i++){
			    it.next();
			}
			Tablero aux =  (Tablero)it.next();
			if(aux.equals(this.elitismo)){
			    return eliminaAleatorio();
			}
			this.individuos.remove(aux);
			this.tam--;
			return aux;
	    }


	     //Metodo que sirve para tener un tablero de manera de seleccion ruleta.
	    public Tablero seleccionRuleta(){
			LinkedList<Double> prob = new LinkedList<Double>();
			LinkedList<Tablero> tab = new LinkedList<Tablero>();
			double aleatorio = this.ruleta.nextDouble()*this.individuos.size();
			Iterator it = this.individuos.iterator();
			double total = 0;
			while(it.hasNext()){
			    Tablero aux = (Tablero)it.next();
			    total += aux.getAptitud();
			}
			it = this.individuos.iterator();
			int pos = 0;
			while(it.hasNext()){
			    tab.add((Tablero)it.next());
			    prob.add((tab.get(pos).getAptitud()));
			    pos++;
			}
			int rn = this.ruleta.nextInt((int)total);
			int inicio = 0;
			int fin = 0;
			Tablero aux2 = tab.peekFirst();
			for(int m = 0; m < pos; m++){
			    fin += prob.get(m);
			    if(rn >= inicio && rn <= fin){
					aux2 = tab.get(m);
				}
			    inicio = fin;
			}
			return aux2;
	    }
	    

	    //Metodo que agrega un tablero al conjunto
	    public void agregaIndividuo(Tablero t){
			this.tam++;
			if(this.elitismo == null || this.elitismo.getAptitud() >= t.getAptitud()){
			    this.elitismo = t;
			}
			    this.individuos.add(t);
	    }

	}

	//Metodo principal del programa
    public static void main(String[] args){
		int tamTablero = 8;
		int muestra = 50;
		if(args.length == 1){
		    try{
			muestra = 50;
			tamTablero = Integer.parseInt(args[0]);
		    }catch(NumberFormatException e){
			System.out.println("Erro: el parámetro que indicó no es un número");
		    }
		}else{
		    muestra = 50;
		    tamTablero = 8;
		}	
		if(args.length == 2){
		    try{
			tamTablero = Integer.parseInt(args[0]);
			muestra = Integer.parseInt(args[1]);
		    }catch(NumberFormatException e){
			System.out.println("Error: el parámetro que indicó no es un número");
		    }
		}else{
		    muestra = 50;
		}
		if(tamTablero < 2){
		    System.out.println("Error: el tamaño del tablero debe ser mayor a 1");
		    System.exit(0);
		}
		if(muestra < 2){
		    System.out.println("Error: el tamaño de la muestra debe ser mayor a 1");
		    System.exit(0);
		}
		Poblacion anterior = new Poblacion(muestra,tamTablero);
		int i = 0;
		boolean optimoEncontrado = false;
		while(i < 2000 && !optimoEncontrado){
		    Poblacion nueva = new Poblacion(0,tamTablero);
		    nueva.setElitismo(anterior.getElitismo());
		    while(anterior.getTam() != nueva.getTam()){
				Tablero tablero1 = anterior.seleccionRuleta();
				Tablero tablero2 = anterior.seleccionRuleta();
				Tablero hijo = tablero1.recombina(tablero2);
				hijo.muta();
				nueva.agregaIndividuo(hijo);
		    }	    
		    anterior = nueva;
		    if(anterior.getElitismo().getAptitud() == 0){
				optimoEncontrado = true;
			}
		    i++;
		    if(i % 50 == 0 || optimoEncontrado){
				System.out.print("La mejor solucion en la iteracion "+"["+i+"]"+" es: [");
				for(int k= 0; k < anterior.getElitismo().getTam(); k++){
					System.out.print(anterior.getElitismo().getReyna(k)+1+",");
					}
				System.out.print("]"+"\n");
				System.out.println("Aptitud = "+anterior.getElitismo().getAptitud());
		    }
		}	
    }



}