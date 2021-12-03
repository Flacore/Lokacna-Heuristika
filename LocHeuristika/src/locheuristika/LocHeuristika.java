package locheuristika;
import java.io.*;
import java.util.*;

public class LocHeuristika {
    //Nazvy suborov
    final static String SuborFI = "Subory/fi.txt";
    final static String SuborCIJ = "Subory/cij.txt";
    
    final static long CAS_KONCA = -1; //cas konca v sekundach
    final static int POCET_KONIEC = -1; //ak -1; Program zbehne celý
    final static int NACITAJ_DOKUMENT = 1; //ak -1; Dokument sa nenačíta
    
    static long startTime;
    
    static int i = 3;
    static int j = 4;
    
    static int [] fi = {6,3,4};
    static int [][] cij={{2,5,2,5},
                         {3,6,7,6},
                         {5,7,3,7}};
    
    static int naj_HUF;
    static int [] zaradene_riesenie;
    
    static int krok;
    
    static int [] zaradene;
    
    public static void main(String[] args) throws IOException { 
        System.out.println("*Lokačná heuristika*:");
        System.out.println("_________________________________________________");
        System.out.println("Nastavovanie systému:");
        
        if(NACITAJ_DOKUMENT != -1){
            System.out.println("Nahráva sa súbor pre fixné náklady.");
            load_fix();
            System.out.println("Nahráva sa súbor pre ceny.");
            load_var();
        }
        
        System.out.println("Nastavujem defaultnú hodnotu účelovej funkcie.");
        naj_HUF=999999999;
        System.out.println("Vymedzujem rozsah v pamäti pre zaradené prvky.");
        zaradene = new int[i];
        System.out.println("Vymedzujem rozsah v pamäti pre výsledné prvky.");
        zaradene_riesenie = new int[i];
        System.out.println("Nulujem počet krokov.");
        krok = 0;
        //TODO kontrola dat
        System.out.println("Nastavujem čas začiatku.");
        startTime = System.nanoTime();
        
        System.out.println("_________________________________________________");
        System.out.println("Jednotlivé účelové funkcie:"); 
        combinations(0);
        System.out.println("_________________________________________________");
        execution();
        System.out.println("_________________________________________________");
    }
    
    private static void load_fix() throws IOException{
        try{
            FileReader fr = new FileReader(SuborFI);
            BufferedReader in = new BufferedReader(fr);
            String riadok;

            int counter = 0;
            while((riadok = in.readLine())!=null){
                int tmp = stoi(riadok);
                if(counter != 0){
                    fi[counter-1]=tmp;
                    counter++;
                }else{
                    i = tmp;
                    fi = new int[i];
                    counter++;
                }
            }

            fr.close();

            if(counter - 1 != i){
                System.out.println("Program padol lebo v súbore nieje dosť dát.");
                System.exit(-1);
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("error " + e);
        }
    }
    
    private static void load_var() throws IOException{
        try{
            FileReader fr = new FileReader(SuborFI);
            BufferedReader in = new BufferedReader(fr);
            String riadok;

            int counter = 0;
            while((riadok = in.readLine())!=null){
                int tmp = stoi(riadok);
                switch(counter){
                    case 0:
                        if(tmp != i){
                            System.out.println("Program padol lebo v súbore nieje dosť dát.");
                            System.exit(-1);
                        }
                        break;
                    case 1:
                        j = tmp;
                        cij = new int[i][j];
                        break;
                    default:
                        StringTokenizer st = new StringTokenizer(riadok, " ");
                        int colum = 0;
                        while (st.hasMoreTokens()) {
                            cij[counter-2][colum] =   Integer.parseInt(st.nextToken());
                            colum++;
                        }
                        break;
                }
                counter++;
            }
            fr.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("error " + e);
        }
    }
    
    private static void execution(){
        System.out.println("Najlepšie nájdené:");    
        System.out.print("[ ");
        for (int k = 0; k < i; k++) {
            System.out.print(zaradene_riesenie[k]+" ");  
        }
        System.out.print("] Hodnota HUF: "+naj_HUF+"\n");
    }
    
    private static void combinations(int index){
        if(index == i){
            int count=0;
            
            for (int k = 0; k < i; k++) {
                count+= zaradene[k];  
            }
            
            if(count>0){
                int tmp = HUF(zaradene);

                System.out.print("[ ");
                for (int k = 0; k < i; k++) {
                    System.out.print(zaradene[k]+" ");  
                }
                System.out.print("] Hodnota HUF: "+tmp+"\n");

                if(tmp<naj_HUF){
                    naj_HUF = tmp;
                    for (int k = 0; k < i; k++) {
                        zaradene_riesenie[k] = zaradene[k];   
                    }  
                }
                
                if(CAS_KONCA > -1 && ((System.nanoTime()-startTime)*1000000000)>=CAS_KONCA){
                    System.out.println("Dosiel cas na vykonavanie programu.");
                    System.out.println("_________________________________________________");
                    execution();
                    System.out.println("_________________________________________________");
                    System.exit(-1);
                }
                
                if(POCET_KONIEC > -1 && krok >= POCET_KONIEC){
                    System.out.println("Program dosiahol max. pocet krokov.");
                    System.out.println("_________________________________________________");
                    execution();
                    System.out.println("_________________________________________________");
                    System.exit(-1);
                }
                
                krok++;
            }
        }else{
            for(int k=0; k<=1; ++k){
                zaradene[index] = k;
                combinations(index+1);
            }
        }
    }
    
    private static int HUF(int [] postavene){
        int huf = 0;
        
        for(int x = 0; x < i; ++x){
            huf+=postavene[x]*fi[x];
        }
        
        for(int x = 0; x < j; ++x){
           int min_naklad=9999; 
           for(int y = 0; y < i; ++y){
               if(postavene[y] == 1 ){
                   if(cij[y][x] < min_naklad){
                       min_naklad = cij[y][x];
                   }
               }
            }
           huf+=min_naklad;
        }
        
        return huf;
    }
    
    private static int stoi(String txt){
        int tmp=0;
            for (int i = 0; i < txt.length(); i++) {
            tmp*=10;
            tmp +=txt.charAt(i)-48;
        }
        return tmp;
    }

    
}
