package filemanager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nemanja Koprivica
 */
public class Functions {

    //Metoda za unos putanje fajla/foldera
    public static String inputPath() {

        Scanner inputPath = new Scanner(System.in);
        String input = inputPath.nextLine();
        return input;
    }

    //Metoda za listanje foldera iz tražene putanje
    public static void list() {

        System.out.println("Folder view");
        System.out.println("Input path to ciew folders content: ");
        File path = new File(inputPath());

        if (path.exists() && path.isDirectory()) {
            String[] list = path.list();
            System.out.println("Folder contains: ");
            for (int i = 0; i < list.length; i++) {
                System.out.println(list[i]);
            }
        } else {
            System.err.println("Folder doesn't exist");
        }
    }

    //Metoda za ispis iformacija o određenom folderu
    public static void info() {

        System.out.println("File/Folder info");
        System.out.println("Input path to view info of file/folder: ");
        File path = new File(inputPath());

        if (!path.exists()) {
            System.err.println("File/Folder doesn't exist!");
        } else {
            System.out.println("Name: " + path.getName());
            System.out.println("Path: " + path.getPath());

            //Vraća veličinu direktorijuma, tj fajlova u direktorijumu, ali bez poddirektorijuma, jer bi za to bila potrebna rekurzija,
            // ili neka od kolekcija. Ako sam lepo razumeo hint zadatka, da nije potrebno računati poddirektorijume.
            if (path.isDirectory()) {
                long size = 0;
                for (File f : path.listFiles()) {
                    size += f.length();
                }
                System.out.println("Size: " + size + " bytes");
            } else {
                System.out.println("Size: " + path.length() + " bytes");
            }

            //Šablon za formatiranje dobijenog vremena(u nastavku koda ga koristimo, prilikom štampanja dobijenog rezultata)
            DateTimeFormatter df = DateTimeFormatter.ofPattern(" dd. MMM yyyy. HH:mm:ss");

            try {
                //Vraća datum i vreme kreiranja foldera, prvo u milisekundama, a zatim, konvertujemo u LocalDateTime, a zatim i formatiramo po želji 
                long timeCr = Files.readAttributes(path.toPath(), BasicFileAttributes.class).creationTime().toMillis();
                Instant instant = Instant.ofEpochMilli(timeCr);  //vreme kreiranja tipa long pretvaramo u mašinsko vreme
                LocalDateTime created = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());   //a mašinsko u LocalDateTime objekat
                System.out.println("Created: " + created.format(df));

                //Vraća datum i vreme poslednje modifikacije foldera (identičan postupak prevođenja vremena, kao i za vreme kreiranja fajla/foldera 
                long timeMod = Files.readAttributes(path.toPath(), BasicFileAttributes.class).lastModifiedTime().toMillis();
                instant = Instant.ofEpochMilli(timeMod);
                LocalDateTime modified = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                System.out.println("Last Modified: " + modified.format(df));

            } catch (IOException ex) {
                Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    //Metoda za kreiranje novog foldera
    public static void createDir() {

        System.out.println("Create Folder");
        System.out.println("Input path to create folder: ");
        File path = new File(inputPath());
        if (path.exists()) {
            System.err.println("Folder already exists");
        } else {
            path.mkdir();   //metoda make directory klase File kojom kreiramo novi folder
            System.out.println("Folder " + path.getName() + " successfully created");
        }
    }

    //Metoda za preimenovanje fajla/foldera
    public static void rename() {

        System.out.println("Rename file/folder");
        System.out.println("Input path for file/folder to be renamed: ");
        File oldPath = new File(inputPath());   //unosimo putanju fajla ili foldera koji hoćemo da preimenujemo
        System.out.println("Input path for a new name of file/folder: ");
        File newPath = new File(inputPath());   //unosimo novu putanju fajla ili foldera
        if (!oldPath.exists()) {
            System.err.println(oldPath.getName() + " doesn't exists");  //proveravamo da li stara putanja postoji, i ako ne izbacuje nam grešku 
        } else if (newPath.exists()) {
            System.err.println(newPath.getName() + " already exists");  //proveravamo, takodje, da li postoji već fajl/folder u koji želimo da preimenujemo stari
        } else if (oldPath.renameTo(newPath)) {                         //ukoliko su prva dva uslova ispunjena, menjamo ime fajla/foldera metodom ranameTo() klase File
            System.out.println("Rename was successfully executed");
        } else {
            System.err.println("Rename failed!");
        }
    }

    //Metoda za kopiranje fajla/foldera
    public static void copy() {
        System.out.println("Copy file/folder");
        System.out.println("Input path for file/folder to be copied: ");
        File oldPath = new File(inputPath());
        System.out.println("Input path and name for new file/folder: ");
        File newPath = new File(inputPath());
        if (!oldPath.exists()) {
            System.err.println("Folder doesn't exists");
            return;
        }
        if (oldPath.isDirectory()) {

            if (!newPath.exists()) {    //ukoliko novi folder ne postoji, pravimo novi
                newPath.mkdirs();
            }
            Path newDirPath = newPath.toPath(); //klasom Path i metodom toPath() iste klase kreiramo objekat sa njegovom apsolutnom putanjom za novi folder
            for (File oldFile : oldPath.listFiles()) {  //Prolazimo kroz fajlove starog foldera
                Path oldDirPath = oldFile.toPath(); //i za njih kreiramo objekat klase Path sa apsolutnom putanjom do fajlova starog direktorijuma
                try {
                    Files.copy(oldDirPath, newDirPath.resolve(oldDirPath.getFileName()));   //Kopiramo iz starog foldera u novi(NAPOMENA: Ovu metodu resolve() sam našao na oracle-ovoj stranici https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html)
                    System.out.println("Folder " + oldDirPath.getFileName() + " is moved!");
                    return;
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        } else if (oldPath.isFile()) {      //Kopiranje fajlova korišćenjem paketa java.nio
            Path oldDirPath = oldPath.toPath();
            Path newDirPath = newPath.toPath();
            try {
                if (!Files.exists(oldDirPath)) {    //Proveravamo ukoliko fajl ne postoji na zadatoj putanji
                    System.err.println("File doesn't exists!");
                    return;
                }
                if (!Files.exists(newDirPath.getParent())) {    //Proveravamo dali postoji putanja gde želimo da iskopiramo fajl
                    System.err.println("Location doesn't exists!");
                    return;
                }
                if (Files.exists(newDirPath)) {     //Proveravamo da li već postoji fajl sa istim imenom, kao naša kopija fajla(da ne bi došlo do toga da već postojeći fajl uništimo novom kopijom)
                System.err.println("File already exists on the location!");
                return;
                }

                Files.copy(oldDirPath, newDirPath); //Ukoliko su svi uslovi ispunjeni, vršimo kopiranje fajla
                System.out.println("File " + oldDirPath.getFileName() + " is copied!"); 

            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }

    //Metoda za premeštanje fajla/foldera (metoda ista kao i copy metoda, uz brisanje starog foldera, ili fajla nakon uspešno izvršene operacije)
    public static void move() {

        System.out.println("Move file/folder");
        System.out.println("Input path for file/folder to be moved: ");
        File oldPath = new File(inputPath());
        System.out.println("Input path and name for new file/folder: ");
        File newPath = new File(inputPath());
        if (!oldPath.exists()) {
            System.err.println("Folder doesn't exists");
            return;
        }
        if (oldPath.isDirectory()) {

            if (!newPath.exists()) {    //ukoliko novi folder ne postoji, pravimo novi
                newPath.mkdirs();
            }
            Path newDirPath = newPath.toPath(); //klasom Path i metodom toPath() iste klase kreiramo objekat sa njegovom apsolutnom putanjom za novi folder
            for (File oldFile : oldPath.listFiles()) {  //Prolazimo kroz fajlove starog foldera
                Path oldDirPath = oldFile.toPath(); //i za njih kreiramo objekat klase Path sa apsolutnom putanjom do fajlova starog direktorijuma
                try {
                    Files.copy(oldDirPath, newDirPath.resolve(oldDirPath.getFileName()));   //Kopiramo iz starog foldera u novi(NAPOMENA: Ovu metodu resolve() sam našao na oracle-ovoj stranici https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html)
                } catch (IOException ex) {
                    System.out.println(ex);
                }
                oldPath.delete();   //  brišemo stari direktorijum, nakon završenog kopiranja
                System.out.println("Folder " + oldDirPath.getFileName() + " is moved!");
                return;
            }
        } else if (oldPath.isFile()) {      //Kopiranje fajlova korišćenjem paketa java.nio
            Path oldDirPath = oldPath.toPath();
            Path newDirPath = newPath.toPath();
            try {
                if (!Files.exists(oldDirPath)) {    //Proveravamo ukoliko fajl ne postoji na zadatoj putanji
                    System.err.println("File doesn't exists!");
                    return;
                }
                if (!Files.exists(newDirPath.getParent())) {    //Proveravamo dali postoji putanja gde želimo da iskopiramo fajl
                    System.err.println("Location doesn't exists!");
                    return;
                }
                if (Files.exists(newDirPath)) {     //Proveravamo da li već postoji fajl sa istim imenom, kao naša kopija fajla(da ne bi došlo do toga da već postojeći fajl uništimo novom kopijom)
                System.err.println("File already exists on the location!");
                return;
                }

                Files.copy(oldDirPath, newDirPath); //Ukoliko su svi uslovi ispunjeni, vršimo kopiranje fajla
                System.out.println("File " + oldDirPath.getFileName() + " is moved!"); 

            } catch (IOException ex) {
                System.out.println(ex);
            }
            oldPath.delete();   //  nakon izvršenog kopiranja fajla, vršimo brisanje fajla
        }
    }

    //Metoda za brisanje fajla/foldera
    public static void delete() {
        System.out.println("Delete file/folder");
        System.out.println("Input path for file/folder to be deleted: ");
        File path = new File(inputPath());

        if (!path.exists()) {   //proveravamo da li fajl/folder postoji, ako ne, izbacujemo grešku
            System.err.println("File/folder doesn't exists!");
        } else {    //proveravamo da li je korisnik siguran da želi da obriše fajl/folder
            System.out.println("Do you realy want to delete " + path.getName() + "? \nPlease type yes if you are sure");
            Scanner input = new Scanner(System.in);
            String answer = input.nextLine();
            if (answer.equalsIgnoreCase("yes")) {
                path.delete();  //pristupamo brisanju fajla/foldera
                System.out.println("File/folder " + path.getName() + " successfully deleted");
            } else {
                System.out.println("File/folder " + path.getName() + " not deleted");
            }
        }
    }
}
