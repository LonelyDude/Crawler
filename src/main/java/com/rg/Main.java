package com.rg;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter absolute path:");
        String fileName = scanner.nextLine();
        System.out.println();

        Crawler crawler = new Crawler();
        crawler.crawl(new File(fileName));
    }

}
