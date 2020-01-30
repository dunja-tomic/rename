import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class rename {

    // Print out the help dialogue with all the commands
    private static void printHelp() {
        System.out.println("(c) 2020 Dunja Tomic");
        System.out.println("Usage: java rename [-option argument1 argument2 ...]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("-help                   :: display this help and exit.");
        System.out.println("-prefix [string]        :: rename the file so that it starts with [string].");
        System.out.println("-suffix [string]        :: rename the file so that it ends with [string]. ");
        System.out.println(
                "-replace [str1] [str2]  :: rename [filename] by replacing all instances of [str1] with [str2].");
        System.out.println("-file [filename]        :: indicate the [filename] to be modified.");
    }

    // Loop through the array of args to find all the file names
    // That is, all the strings that follow "-file" and are not options
    private static ArrayList<String> findFileNames(String[] args) {
        ArrayList<String> fileNames = new ArrayList<String>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-file")) {
                for (int j = i + 1; j < args.length; j++) {
                    // It's an option, so exit
                    if (args[j].startsWith("-")) {
                        break;
                    }
                    // Else add to the list of file names
                    fileNames.add(args[j]);
                }
            }
        }
        return fileNames;
    }

    // Rename the file called oldName into newName
    // Throws an error if the renaming is unsuccessful
    private static void renameFile(String oldName, String newName) throws Exception {
        File oldFile = new File(oldName);
        File newFile = new File(newName);

        boolean success = oldFile.renameTo(newFile);

        if (!success) {
            throw new Exception("Error renaming file: " + oldName);
        }
    }

    // Format the param to be in the correct format
    // If the param is @date, returns the current date
    // If the param is @time, returns the currect time
    // Otherwise it just returns the original param
    private static String formatParameter(final String param) {
        // @date param
        if (param.equals("@date")) {
            final DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            final Calendar calobj = Calendar.getInstance();
            return df.format(calobj.getTime());
        }
        // @time param
        else if (param.equals("@time")) {
            final DateFormat df = new SimpleDateFormat("HH-mm-ss");
            final Calendar calobj = Calendar.getInstance();
            return df.format(calobj.getTime());
        }
        // String param
        else {
            return param;
        }
    }

    // If there are multiple params, it concatenates them together
    // Index is the index in args of the first param
    // ie a b c -> abc
    private static String getParam(String[] args, int index) {
        String param = "";
        for (int j = index + 1; j < args.length; j++) {
            // No more params, break
            if (args[j].startsWith("-")) {
                break;
            }

            // Format the arg and add it to the param
            param = param + formatParameter(args[j]);
        }
        // Format again, for good measure
        return formatParameter(param);
    }

    public static void main(String[] args) {
        // If no args are specified, print the help dialogue
        if (args.length == 0) {
            printHelp();
            return;
        } else {
            try {
                // Get the names of all the files we want to rename
                ArrayList<String> fileNames = findFileNames(args);
                int numFiles = fileNames.size();

                if (numFiles == 0) {
                    throw new Exception("Please specify a file name.");
                }

                // Loop through the list of args
                for (int i = 0; i < args.length; i++) {
                    // Help option
                    if (Arrays.asList(args).contains("-help")) {
                        printHelp();
                        return;
                    }
                    // Prefix option
                    else if (args[i].equals("-prefix")) {
                        // Get the param
                        String prefix = getParam(args, i);

                        // If no param was provided, throw an error
                        if (prefix.isEmpty()) {
                            throw new Exception("Invalid parameters: prefix requires at least one parameter.");
                        }

                        // Add the prefix to each file in fileNames
                        for (int f = 0; f < numFiles; f++) {
                            System.out.println("Applying prefix: renaming " + fileNames.get(f) + " to " + prefix
                                    + fileNames.get(f));

                            renameFile(fileNames.get(f), prefix + fileNames.get(f));
                            fileNames.set(f, prefix + fileNames.get(f));
                        }

                    }
                    // Suffix option
                    else if (args[i].equals("-suffix")) {
                        // Get the param
                        String suffix = getParam(args, i);

                        // If no param was provided, throw an error
                        if (suffix.isEmpty()) {
                            throw new Exception("Invalid parameters: suffix requires at least one parameter.");
                        }

                        // Add the suffix to each file in fileNames
                        for (int f = 0; f < numFiles; f++) {
                            System.out.println("Applying suffix: renaming " + fileNames.get(f) + " to "
                                    + fileNames.get(f) + suffix);

                            renameFile(fileNames.get(f), fileNames.get(f) + suffix);
                            fileNames.set(f, fileNames.get(f) + suffix);
                        }

                    }
                    // Replace option
                    else if (args[i].equals("-replace")) {
                        // If there aren't two params, throw an error
                        if (i + 1 >= args.length || i + 2 >= args.length || args[i + 1].startsWith("-")
                                || args[i + 2].startsWith("-")) {
                            throw new Exception("Invalid parameters: replace requires exactly two parameters.");
                        }

                        // Format the two parameters
                        String src = formatParameter(args[i + 1]);
                        String dst = formatParameter(args[i + 2]);

                        // Replace src with dst for each file in fileNames
                        for (int f = 0; f < numFiles; f++) {
                            System.out.println("Applying replace: renaming " + fileNames.get(f) + " to "
                                    + fileNames.get(f).replace(src, dst));

                            // Rename the file
                            renameFile(fileNames.get(f), fileNames.get(f).replace(src, dst));
                            fileNames.set(f, fileNames.get(f).replace(src, dst));
                        }

                    }
                    // If its an invalid option, throw an error
                    else if (args[i].startsWith("-") && !args[i].equals("-file")) {
                        throw new Exception("Invalid option: " + args[i]
                                + " is an invalid option. Run with -help to view available options.");
                    }

                    // Else it's a param or -file so do nothing
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}