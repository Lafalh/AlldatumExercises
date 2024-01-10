package org.alldatum.excercise2;

import java.util.Arrays;

public class Excercise_2 {
    public static void main(String[] args){
        //Declaramos un Array con el "1000-Digit".
        char[] num_array = "7316717653133062491922511967442657474235534919493496983520312774506326239578318016984801869478851843858615607891129494954595017379583319528532088055111254069874715852386305071569329096329522744304355766896648950445244523161731856403098711121722383113622298934233803081353362766142828064444866452387493035890729629049156044077239071381051585930796086670172427121883998797908792274921901699720888093776657273330010533678812202354218097512545405947522435258490771167055601360483958644670632441572215539753697817977846174064955149290862569321978468622482839722413756570560574902614079729686524145351004748216637048440319989000889524345065854122758866688116427171479924442928230863465674813919123162824586178664583591245665294765456828489128831426076900422421902267105562632111110937054421750694165896040807198403850962455444362981230987879927244284909188845801561660979191338754992005240636899125607176060588611646710940507754100225698315520005593572972571636269561882670428252483600823257530420752963450".toCharArray();

        //Variable para el mayor producto.
        long greater_product = 0;

        //Variable para la cantidad de números adyacentes a buscar.
        long adjacent = num_array.length - 13;

        //Array Auxiliar para los "Slices" del número mayor
        String big_prod_string = null;

        //LOOP buscando los "Slices" en el "1000-Digit"
        for(var j = 0;j <= adjacent; j++){

            //Sacamos un nuevo array de 13 números consecutivos en el num_array
            char[] arr_sliced = Arrays.copyOfRange(num_array,j,j+13);

            //Llamamos a la función para obtener el producto de los números adyacentes
            long prod = multi(arr_sliced);

            //Si el Producto obtenido es mayor, remplaza al producto anterior y almacena los números que generaron ese producto
            if(prod > greater_product) {
                greater_product = prod;
                big_prod_string = new String(arr_sliced);
            }
        }

        //Imprime los valores del producto mayor para comprobarlos
        System.out.print("Array: " + big_prod_string + "\t Greatest Product: "+ greater_product + "\n");

    }

    //FUNCIÓN encargada de obtener el producto de los números adyacentes
    public static long multi(char[] array){
        //inicializamos la variable que almacena el producto
        long prod = 1;

        //LOOP que multiplica cada valor del array de 13 dígitos
        for (long i = 0; i < array.length; i++){
            char a = array[(int) i];
            prod = prod * Character.getNumericValue(array[(int) i]);
        }
        //Regresamos el producto final
        return prod;
    }
}