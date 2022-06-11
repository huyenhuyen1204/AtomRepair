package main;

import java.util.Scanner;

public class test {
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        int a = scan.nextInt();
        int b = scan.nextInt();
        for (int i = 0; i <= a; i++) {
            for (int j = 0; j <= b; j++) {
                int c = i*5 + j;
                System.out.println(c);
            }
        }
    }
}