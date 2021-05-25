package filemanager;

import static filemanager.FileManager.Commands.COPY;
import static filemanager.FileManager.Commands.CREATE_DIR;
import static filemanager.FileManager.Commands.DELETE;
import static filemanager.FileManager.Commands.INFO;
import static filemanager.FileManager.Commands.LIST;
import static filemanager.FileManager.Commands.MOVE;
import static filemanager.FileManager.Commands.RENAME;
import java.util.Scanner;

/**
 *
 * @author Nemanja Koprivica
 */
public class FileManager {

    enum Commands {
        LIST, INFO, CREATE_DIR, RENAME, COPY, MOVE, DELETE
    }

    public static void main(String[] args) {

        System.out.println("LIST, INFO, CREATE_DIR, RENAME, COPY, MOVE, DELETE");
        System.out.println("Input command: ");
        Scanner input = new Scanner(System.in);
        String type = input.nextLine();
       
        Commands comm = null;
        try {
            comm = Commands.valueOf(type);
        } catch (Exception e) {
            System.err.println("Command doesn't exists!");
        }
        if (comm.equals(LIST)) {
            Functions.list();
        }
        if (comm.equals(INFO)) {
            Functions.info();
        }
        if (comm.equals(CREATE_DIR)) {
            Functions.createDir();
        }
        if (comm.equals(RENAME)) {
            Functions.rename();
        }
        if (comm.equals(COPY)) {
            Functions.copy();
        }
        if (comm.equals(MOVE)) {
            Functions.move();
        }
        if (comm.equals(DELETE)) {
            Functions.delete();
        }
    }
}
