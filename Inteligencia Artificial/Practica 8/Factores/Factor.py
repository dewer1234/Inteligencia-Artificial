
#Clase Variable

class Variable:

    def __init__(self,nombre,valores):

        self.nombre = nombre
        self.valores = valores

    def __str__(self):
        s = ""
        for v in self.valores:
            s += str(v) + ","
        s = s[:-1]
        return "{" + str(self.nombre) + " : " + s + "}" 

    def __repr__(self):
        return str(self)

# Metodo que nos dice si una variable se encuentra en la lista de variables 
def seEncuentra(elem,lista):
    for e in lista:
        if e.nombre == elem.nombre:
            return True
    return False

# Metodo que recibe una variable y regresa copia
def generaCopia(var):
    clon = Variable(var.nombre,[])
    for v in var.valores:
        clon.valores.append(v)
    return clon

# Metodo que da los valores posibles de una lista de variables en forma de Matriz
def tabla_de_valores(variables):
    tabla = []
    for v in variables[0].valores:
        tabla.append([v])

    for i in range(1,len(variables)):
        tablatemp = []
        for renglon in tabla:
            for j in range(len(variables[i].valores)):
                renglon2 = []
                for v in renglon:
                    renglon2.append(v)                    
                tablatemp.append(renglon2)
                
        for k in range(len(tabla)):
            for l in range(len(variables[i].valores)):
                n = k*len(variables[i].valores)+l
                tablatemp[n].append(variables[i].valores[l])

        tabla = tablatemp
    return tabla

#Clase Factor

class Factor:

    def __init__(self,alcance,valores):

        self.alcance = alcance
        self.valores = valores
        self.tabla_valores = tabla_de_valores(alcance)

    def __str__(self):
        s = ""
        for var in self.alcance:
            s += " " + var.nombre + " "
        s += "\n"
        for i in range(len(self.tabla_valores)):
            s += str(self.tabla_valores[i]) + "|" + str(self.valores[i]) + "\n"
        return s

    def __repr__(self):
        return str(self)


# Metodo que recibe un diccionario con variables y regresa el indice en la tabla de valores que representa
    def indice(self,dict):
        ind = []
        ret = 0
        for var in self.alcance:
            ind.append(var.valores.index(dict[var.nombre]))
        for i in range(len(self.alcance)):
            prod = 1
            for j in range(i+1,len(self.alcance)):
                prod *= len(self.alcance[j].valores)
            ret += (prod*ind[i])
        return ret

# Metodo que hace la multiplicacion de factores    
    def multiplicacion(self,factor):
        newalcance = []
        for v in self.alcance:
            newalcance.append(generaCopia(v))
        for v in factor.alcance:
            if not seEncuentra(v,self.alcance):
                newalcance.append(generaCopia(v))
        newfact = Factor(newalcance,[])
        newfact.valores = [0] * len(newfact.tabla_valores)
        for renglon in newfact.tabla_valores:
            dir = {}
            for i in range(len(newfact.alcance)):
                dir[newfact.alcance[i].nombre] = renglon[i]
            dir1 = {}
            for var in self.alcance:
                if dir[var.nombre] != None:
                    dir1[var.nombre] = dir[var.nombre]
            dir2 = {}
            for var in factor.alcance:
                if dir[var.nombre] != None:
                    dir2[var.nombre] = dir[var.nombre]        
            newfact.valores[newfact.indice(dir)] = self.valores[self.indice(dir1)] * factor.valores[factor.indice(dir2)]
            
        return newfact

#Metodo que hace la reduccion de factores    
    def reduccion(self,variable,valor):
        if len(self.alcance) == 1:
            dic = {}
            dic[variable.nombre] = valor
            newvar = Variable(variable.nombre,[valor])            
            newfactor = Factor([newvar],[self.valores[self.indice(dic)]])
            return newfactor

        newalcance = []
        for v in self.alcance:
            if v.nombre != variable.nombre:
                newalcance.append(generaCopia(v))
        newfact = Factor(newalcance,[])
        newfact.valores = [0] * len(newfact.tabla_valores)        
        for i in range(len(newfact.tabla_valores)):
            dir = {variable.nombre:valor}
            for j in (range(len(newfact.alcance))):
                dir[newfact.alcance[j].nombre] = newfact.tabla_valores[i][j]
            newfact.valores[i] = self.valores[self.indice(dir)]
        return newfact

#Metodo que hace la normalizacion de factores    
    def normalizacion(self):
        newalcance = []
        for v in self.alcance:
            newalcance.append(generaCopia(v))
        newvalores = []
        total = 0
        for val in self.valores:
            newvalores.append(val)
            total += val
        newfact = Factor(newalcance,newvalores)        
        for i in range(len(newfact.valores)):
            newfact.valores[i] /= total 
        return newfact

#Metodo que hace la marginalizacion de factores
    def marginalizacion(self,variable):
        newalcance = []
        for v in self.alcance:
            if v.nombre != variable.nombre:
                newalcance.append(generaCopia(v))
        newfact = Factor(newalcance,[])
        newfact.valores = [0] * len(newfact.tabla_valores)
        for i in range(len(newfact.tabla_valores)):
            suma = 0
            dir = {}
            for j in (range(len(newfact.alcance))):
                dir[newfact.alcance[j].nombre] = newfact.tabla_valores[i][j]                
            for val in variable.valores:
                dir[variable.nombre] = val
                suma += self.valores[self.indice(dir)]
            dir.pop(variable.nombre,None)
            newfact.valores[newfact.indice(dir)] = suma
        return newfact

varr = Variable("R",[0,1])
vart = Variable("T",[0,1])
vara = Variable("A",[0,1])
R = Factor([varr],[0.999,0.001])
A_RT = Factor([vara,varr,vart],[0.999,0.71,0.06,0.05,0.001,0.29,0.94,0.95])

print("FACTOR R")
print(R)
print("FACTOR A R T")
print(A_RT)
#Prueba de mulplicacion
print("Prueba de multiplacion")
print(R.multiplicacion(A_RT))
#Prueba de reducir
print("Prueba de reduccion")
print(R.multiplicacion(A_RT).reduccion(varr,0))
#prueba de normalizacion
print("Prueba de normalizacion")
print(R.multiplicacion(A_RT).normalizacion())
#Prueba de marginalizacion
print("Prueba de marginalizacion")
print(R.multiplicacion(A_RT).marginalizacion(vara))

//import random
#Algoritmo geneticos
def creaMatriz(n,m, dato):
     matriz = []
     for i in range(n):
         a = [dato]*m
         matriz.append(a)
     return matriz

def matrizstr(matriz):
     cadena = ''
     for i in range(len(matriz)):
         cadena += '['
         for j in range(len(matriz[i])):
             cadena += '{:>4s}'.format(str(matriz[i][j]))
         cadena += ']\n'
     return cadena

class booleanos:

    def __init__(self,num,booleano):
        num = self.num
        booleano = self.booleano
    
class clausula:

    def __init__(self,val1,val2,val3):
        val1 = booleanos(val1,random.randrange(0,1))
        val2 = booleanos(val1,random.randrange(0,1))
        val3 = booleanos(val1,random.randrange(0,1))
        

class Poblacion:
    
    def __init__(self,matriz):
        numclausulas = random.randrange(100,200)
        variables = random.randrange(50,60)
        x1 = random.randrange(50,60)
        x2 = random.randrange(50,60)
        x3 = random.randrange(50,60)
        matriz = creaMatriz(numclausulas,variables,clausula(x1,x2,x3))