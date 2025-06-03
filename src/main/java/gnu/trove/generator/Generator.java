package gnu.trove.generator;

import java.io.*;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Generator {
    private static final WrapperInfo[] WRAPPERS = new WrapperInfo[]{new WrapperInfo("byte", "Byte", "MAX_VALUE", "MIN_VALUE")};
    private static final Pattern PATTERN_v = Pattern.compile("#v#");
    private static final Pattern PATTERN_V = Pattern.compile("#V#");
    private static final Pattern PATTERN_VC = Pattern.compile("#VC#");
    private static final Pattern PATTERN_VT = Pattern.compile("#VT#");
    private static final Pattern PATTERN_VMAX = Pattern.compile("#VMAX#");
    private static final Pattern PATTERN_VMIN = Pattern.compile("#VMIN#");
    private static final Pattern PATTERN_V_UNDERBAR = Pattern.compile("_V_");
    private static final Pattern PATTERN_k = Pattern.compile("#k#");
    private static final Pattern PATTERN_K = Pattern.compile("#K#");
    private static final Pattern PATTERN_KC = Pattern.compile("#KC#");
    private static final Pattern PATTERN_KT = Pattern.compile("#KT#");
    private static final Pattern PATTERN_KMAX = Pattern.compile("#KMAX#");
    private static final Pattern PATTERN_KMIN = Pattern.compile("#KMIN#");
    private static final Pattern PATTERN_K_UNDERBAR = Pattern.compile("_K_");
    private static final Pattern PATTERN_e = Pattern.compile("#e#");
    private static final Pattern PATTERN_E = Pattern.compile("#E#");
    private static final Pattern PATTERN_EC = Pattern.compile("#EC#");
    private static final Pattern PATTERN_ET = Pattern.compile("#ET#");
    private static final Pattern PATTERN_EMAX = Pattern.compile("#EMAX#");
    private static final Pattern PATTERN_EMIN = Pattern.compile("#EMIN#");
    private static final Pattern PATTERN_E_UNDERBAR = Pattern.compile("_E_");
    private static File root_output_dir;

    public static void main(String[] args) throws Exception {
        File input_directory = new File("C:\\Users\\Administrator\\Desktop\\trove-3.0.0\\templates");
        File output_directory = new File("C:\\Users\\Administrator\\Desktop\\trove-3.0.0\\templates2");
        if (!input_directory.exists()) {
            System.err.println("Directory \"" + input_directory + "\" not found.");
            System.exit(-1);
            return;
        }
        if (!output_directory.exists()) {
            Generator.makeDirs(output_directory);
        }
        root_output_dir = output_directory;
        System.out.println("Removing contents of \"" + output_directory + "\"...");
        Generator.cleanDir(output_directory);
        Generator.scanForFiles(input_directory, output_directory);
    }

    private static void makeDirs(File directory) {
        if (directory.exists() && !directory.isDirectory()) {
            throw new IllegalArgumentException(directory + " not a directory");
        }
        if (directory.exists()) {
            return;
        }
        if (!directory.mkdirs()) {
            throw new IllegalStateException("Could not create directories " + directory);
        }
    }

    private static void scanForFiles(File input_directory, File output_directory) throws IOException {
        File[] files;
        File[] fileArray = files = input_directory.listFiles();
        int n = files.length;
        int n2 = 0;
        while (n2 < n) {
            File file = fileArray[n2];
            if (!file.isHidden()) {
                if (file.isDirectory()) {
                    if (!file.getName().equals("CVS")) {
                        Generator.scanForFiles(file, new File(output_directory, file.getName()));
                    }
                } else {
                    Generator.processFile(file, output_directory);
                }
            }
            ++n2;
        }
    }

    private static void processFile(File input_file, File output_directory) throws IOException {
        System.out.println("Process file: " + input_file);
        String content = Generator.readFile(input_file);
        String file_name = input_file.getName();
        file_name = file_name.replaceAll("\\.template", ".java");
        File output_file = new File(output_directory, file_name);
        if (file_name.contains("_K_")) {
            Generator.processKVMarkers(content, output_directory, file_name);
        } else if (file_name.contains("_E_")) {
            Generator.processEMarkers(content, output_directory, file_name);
        } else {
            if (input_file.lastModified() < output_file.lastModified()) {
                System.out.println("File " + output_file + " up to date, not processing input");
                return;
            }
            StringBuilder processed_replication_output = new StringBuilder();
            Map<Integer, String> replicated_blocks = Generator.findReplicatedBlocks(content, processed_replication_output);
            if (replicated_blocks != null) {
                content = Generator.processReplication(processed_replication_output.toString(), replicated_blocks);
            }
            Generator.writeFile(content, output_file);
        }
    }

    private static void processKVMarkers(String content, File output_dir, String file_name) throws IOException {
        WrapperInfo[] wrapperInfoArray = WRAPPERS;
        int n = WRAPPERS.length;
        int n2 = 0;
        while (n2 < n) {
            WrapperInfo info = wrapperInfoArray[n2];
            String k = info.primitive;
            String KT = info.class_name;
            String K = Generator.abbreviate(KT);
            String KC = K.toUpperCase();
            String KMAX = info.max_value;
            String KMIN = info.min_value;
            String out = content;
            out = PATTERN_k.matcher(out).replaceAll(k);
            out = PATTERN_K.matcher(out).replaceAll(K);
            out = PATTERN_KC.matcher(out).replaceAll(KC);
            out = PATTERN_KT.matcher(out).replaceAll(KT);
            out = PATTERN_KMAX.matcher(out).replaceAll(KMAX);
            out = PATTERN_KMIN.matcher(out).replaceAll(KMIN);
            String out_file_name = "T" + file_name;
            out_file_name = PATTERN_K_UNDERBAR.matcher(out_file_name).replaceAll(K);
            WrapperInfo[] wrapperInfoArray2 = WRAPPERS;
            int n3 = WRAPPERS.length;
            int n4 = 0;
            while (n4 < n3) {
                StringBuilder processed_replication_output;
                WrapperInfo jinfo = wrapperInfoArray2[n4];
                String v = jinfo.primitive;
                String VT = jinfo.class_name;
                String V = Generator.abbreviate(VT);
                String VC = V.toUpperCase();
                String VMAX = jinfo.max_value;
                String VMIN = jinfo.min_value;
                String vout = out;
                vout = PATTERN_v.matcher(vout).replaceAll(v);
                vout = PATTERN_V.matcher(vout).replaceAll(V);
                vout = PATTERN_VC.matcher(vout).replaceAll(VC);
                vout = PATTERN_VT.matcher(vout).replaceAll(VT);
                String processed_output = PATTERN_VMIN.matcher(vout = PATTERN_VMAX.matcher(vout).replaceAll(VMAX)).replaceAll(VMIN);
                Map<Integer, String> replicated_blocks = Generator.findReplicatedBlocks(processed_output, processed_replication_output = new StringBuilder());
                if (replicated_blocks != null) {
                    processed_output = Generator.processReplication(processed_replication_output.toString(), replicated_blocks);
                }
                String processed_filename = PATTERN_V_UNDERBAR.matcher(out_file_name).replaceAll(V);
                Generator.writeFile(processed_output, new File(output_dir, processed_filename));
                ++n4;
            }
            ++n2;
        }
    }

    private static void processEMarkers(String content, File output_dir, String file_name) throws IOException {
        WrapperInfo[] wrapperInfoArray = WRAPPERS;
        int n = WRAPPERS.length;
        int n2 = 0;
        while (n2 < n) {
            WrapperInfo info = wrapperInfoArray[n2];
            String e = info.primitive;
            String ET = info.class_name;
            String E = Generator.abbreviate(ET);
            String EC = E.toUpperCase();
            String EMAX = info.max_value;
            String EMIN = info.min_value;
            String out = content;
            out = PATTERN_e.matcher(out).replaceAll(e);
            out = PATTERN_E.matcher(out).replaceAll(E);
            out = PATTERN_EC.matcher(out).replaceAll(EC);
            out = PATTERN_ET.matcher(out).replaceAll(ET);
            out = PATTERN_EMAX.matcher(out).replaceAll(EMAX);
            String processed_output = PATTERN_EMIN.matcher(out).replaceAll(EMIN);
            String out_file_name = "T" + file_name;
            out_file_name = PATTERN_E_UNDERBAR.matcher(out_file_name).replaceAll(E);
            StringBuilder processed_replication_output = new StringBuilder();
            Map<Integer, String> replicated_blocks = Generator.findReplicatedBlocks(processed_output, processed_replication_output);
            if (replicated_blocks != null) {
                processed_output = Generator.processReplication(processed_replication_output.toString(), replicated_blocks);
            }
            Generator.writeFile(processed_output, new File(output_dir, out_file_name));
            ++n2;
        }
    }

    static String processReplication(String content, Map<Integer, String> replicated_blocks) {
        for (Map.Entry<Integer, String> entry : replicated_blocks.entrySet()) {
            StringBuilder entry_buffer = new StringBuilder();
            boolean first_loop = true;
            int i = 0;
            while (i < WRAPPERS.length) {
                WrapperInfo info = WRAPPERS[i];
                String k = info.primitive;
                String KT = info.class_name;
                String K = Generator.abbreviate(KT);
                String KC = K.toUpperCase();
                String KMAX = info.max_value;
                String KMIN = info.min_value;
                int j = 0;
                while (j < WRAPPERS.length) {
                    boolean uses_e;
                    String out;
                    WrapperInfo jinfo = WRAPPERS[j];
                    String v = jinfo.primitive;
                    String VT = jinfo.class_name;
                    String V = Generator.abbreviate(VT);
                    String VC = V.toUpperCase();
                    String VMAX = jinfo.max_value;
                    String VMIN = jinfo.min_value;
                    String before_e = out = entry.getValue();
                    out = Pattern.compile("#e#").matcher(out).replaceAll(k);
                    out = Pattern.compile("#E#").matcher(out).replaceAll(K);
                    out = Pattern.compile("#ET#").matcher(out).replaceAll(KT);
                    out = Pattern.compile("#EC#").matcher(out).replaceAll(KC);
                    out = Pattern.compile("#EMAX#").matcher(out).replaceAll(KMAX);
                    out = Pattern.compile("#EMIN#").matcher(out).replaceAll(KMIN);
                    boolean bl = uses_e = !out.equals(before_e);
                    if (uses_e && j != 0) break;
                    out = Pattern.compile("#v#").matcher(out).replaceAll(v);
                    out = Pattern.compile("#V#").matcher(out).replaceAll(V);
                    out = Pattern.compile("#VT#").matcher(out).replaceAll(VT);
                    out = Pattern.compile("#VC#").matcher(out).replaceAll(VC);
                    out = Pattern.compile("#VMAX#").matcher(out).replaceAll(VMAX);
                    out = Pattern.compile("#VMIN#").matcher(out).replaceAll(VMIN);
                    out = Pattern.compile("#k#").matcher(out).replaceAll(k);
                    out = Pattern.compile("#K#").matcher(out).replaceAll(K);
                    out = Pattern.compile("#KT#").matcher(out).replaceAll(KT);
                    out = Pattern.compile("#KC#").matcher(out).replaceAll(KC);
                    out = Pattern.compile("#KMAX#").matcher(out).replaceAll(KMAX);
                    out = Pattern.compile("#KMIN#").matcher(out).replaceAll(KMIN);
                    if (first_loop) {
                        first_loop = false;
                    } else {
                        entry_buffer.append("\n\n");
                    }
                    entry_buffer.append(out);
                    ++j;
                }
                ++i;
            }
            content = Pattern.compile("#REPLICATED" + entry.getKey() + "#").matcher(content).replaceAll(entry_buffer.toString());
        }
        return content;
    }

    private static void writeFile(String content, File output_file) throws IOException {
        boolean need_to_move;
        File parent = output_file.getParentFile();
        Generator.makeDirs(parent);
        File temp = File.createTempFile("trove", "gentemp", new File(System.getProperty("java.io.tmpdir")));
        BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
        writer.write(content);
        writer.close();
        if (output_file.exists()) {
            boolean matches;
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                byte[] current_file = Generator.digest(output_file, digest);
                byte[] new_file = Generator.digest(temp, digest);
                matches = Arrays.equals(current_file, new_file);
            } catch (NoSuchAlgorithmException ex) {
                System.err.println("WARNING: Couldn't load digest algorithm to compare new and old template. Generation will be forced.");
                matches = false;
            }
            need_to_move = !matches;
        } else {
            need_to_move = true;
        }
        if (need_to_move) {
            Generator.delete(output_file);
            Generator.copyFile(temp, output_file);
            System.out.println("  Wrote: " + Generator.simplifyPath(output_file));
        } else {
            System.out.println("  Skipped: " + Generator.simplifyPath(output_file));
            Generator.delete(temp);
        }
    }

    private static void delete(File output_file) {
        if (!output_file.exists()) {
            return;
        }
        if (!output_file.delete()) {
            throw new IllegalStateException("Could not delete " + output_file);
        }
    }

    private static byte[] digest(File file, MessageDigest digest) throws IOException {
        digest.reset();
        byte[] buffer = new byte[1024];
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            int read = in.read(buffer);
            while (read >= 0) {
                digest.update(buffer, 0, read);
                read = in.read(buffer);
            }
            byte[] byArray = digest.digest();
            return byArray;
        } finally {
            try {
                in.close();
            } catch (IOException iOException) {
            }
        }
    }

    private static String abbreviate(String type) {
        if (type.equals("Integer")) {
            return "Int";
        }
        if (type.equals("Character")) {
            return "Char";
        }
        return type;
    }

    private static String readFile(File input_file) throws IOException {
        if (!input_file.exists()) {
            throw new NullPointerException("Couldn't find: " + input_file);
        }
        BufferedReader reader = null;
        try {
            String line;
            reader = new BufferedReader(new FileReader(input_file));
            StringBuilder out = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append("\n");
            }
            String string = out.toString();
            return string;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException iOException) {
                }
            }
        }
    }

    static Map<Integer, String> findReplicatedBlocks(String content_in, StringBuilder content_out) throws IOException {
        String line;
        HashMap<Integer, String> to_return = null;
        BufferedReader reader = new BufferedReader(new StringReader(content_in));
        StringBuilder buffer = new StringBuilder();
        boolean in_replicated_block = false;
        boolean need_newline = false;
        while ((line = reader.readLine()) != null) {
            if (!in_replicated_block && line.startsWith("====START_REPLICATED_CONTENT #")) {
                in_replicated_block = true;
                need_newline = false;
                if (content_out.length() == 0) {
                    content_out.append(buffer);
                }
                buffer = new StringBuilder();
                continue;
            }
            if (in_replicated_block && line.startsWith("=====END_REPLICATED_CONTENT #")) {
                int number_start_index = "=====END_REPLICATED_CONTENT #".length();
                int number_end_index = line.indexOf("=", number_start_index);
                String number = line.substring(number_start_index, number_end_index);
                Integer number_obj = Integer.valueOf(number);
                if (to_return == null) {
                    to_return = new HashMap<Integer, String>();
                }
                to_return.put(number_obj, buffer.toString());
                in_replicated_block = false;
                need_newline = false;
                continue;
            }
            if (need_newline) {
                buffer.append("\n");
            } else {
                need_newline = true;
            }
            buffer.append(line);
        }
        return to_return;
    }

    private static String simplifyPath(File file) {
        String output_string = root_output_dir.toString();
        String file_string = file.toString();
        return file_string.substring(output_string.length() + 1);
    }

    private static void cleanDir(File directory) {
        File[] fileArray = directory.listFiles();
        int n = fileArray.length;
        int n2 = 0;
        while (n2 < n) {
            File file = fileArray[n2];
            if (file.isDirectory()) {
                Generator.cleanDir(file);
                Generator.delete(file);
            }
            Generator.delete(file);
            ++n2;
        }
    }

    private static void copyFile(File source, File dest) throws IOException {
        FileChannel srcChannel = new FileInputStream(source).getChannel();
        FileChannel dstChannel = new FileOutputStream(dest).getChannel();
        dstChannel.transferFrom(srcChannel, 0L, srcChannel.size());
        srcChannel.close();
        dstChannel.close();
    }

    private static class WrapperInfo {
        final String primitive;
        final String class_name;
        final String max_value;
        final String min_value;

        WrapperInfo(String primitive, String class_name, String max_value, String min_value) {
            this.primitive = primitive;
            this.class_name = class_name;
            this.max_value = class_name + "." + max_value;
            this.min_value = class_name + "." + min_value;
        }
    }
}

