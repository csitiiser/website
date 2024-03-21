package compiler;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class WebsiteCompiler {

    public static String assetsDirectory = "assets";
    public static String templatesDirectory = "templates";
    public static String blogSourcesDirectory = "blog";
    public static String sourceDirectory = "src";
    public static String buildDirectory = "build";
    public static boolean localBuild = true;
    public static String localhost = "http://127.0.0.1:5500/";
    public static String deployHost = "https://csit.github.io/website/";


    public static void main(String[] args) {
        System.out.println("CSIT Website Compile System");
        System.out.println();

        Vector<Vector<String>> inputs = new Vector<>(1,1);
        Vector<String> names = new Vector<>(1,1);
        String prefix = localBuild ? localhost : deployHost;

        System.out.println("Compiling Blog CMS System ...");
        Vector<File> sources = getBlogSources();
        for(File article : sources) {
            inputs.addElement(compileBlog(article));
            names.addElement(article.getName().replace(".blog", ""));
        }

        System.out.println("Reading source pages ... ");
        File folder = new File(sourceDirectory);
        File[] listOfFiles = folder.listFiles();
        for (File listOfFile : listOfFiles)
            if(listOfFile.getName().endsWith(".src")) {
                inputs.addElement(read(listOfFile.getPath()));
                names.addElement(listOfFile.getName().replace(".src", ""));
            }

        System.out.println("Compiling template structures ... ");
        Vector<Vector<String>> tCompiledInputs = new Vector<>(1,1);
        for(Vector<String> inp : inputs) tCompiledInputs.addElement(processTemplates(inp));

        System.out.println("Compiling asset links ... " );
        Vector<Vector<String>> aCompiledInputs = new Vector<>(1,1);
        Vector<String[]> assets = getAssetList();
        for(Vector<String> inp : tCompiledInputs) aCompiledInputs.addElement(processAssetLinks(inp, assets));

        System.out.println("Compiling hyperlinks ... ");
        Vector<Vector<String>> hCompiledInputs = new Vector<>(1,1);
        for(Vector<String> inp : aCompiledInputs) hCompiledInputs.addElement(processLinks(inp, names, prefix));

        System.out.println("Writing compiled pages ... ");
        for(int i = 0; i < inputs.size(); i++)
            if(localBuild) write(buildDirectory+"/"+names.elementAt(i)+".html", hCompiledInputs.elementAt(i));
            else minwrite(buildDirectory+"/"+names.elementAt(i)+".html", hCompiledInputs.elementAt(i));

        System.out.println("Verifying compilation ... ");
        for(int i = 0; i < inputs.size(); i++)
            generateWarnings(aCompiledInputs.elementAt(i), names.elementAt(i)+".html");
    }

    /* Blog CMS */
    public static Vector<String> compileBlog(File f) {
        Vector<String> output = new Vector<>(1,1);
        Vector<Vector<String>> sections = new Vector<>(1,1);
        Vector<String> structure = read(f.getPath());
        Vector<String> navigation_structure = new Vector<>(1,1);

        String blogName = structure.elementAt(0);
        String blogAuthor = structure.elementAt(1);
        String blogPublishDate = structure.elementAt(2);

        String blog_id = f.getName().replace(".blog", "").trim();

        for(int i = 3; i < structure.size(); i++) {
            if(structure.elementAt(i).trim().isEmpty()) continue;
            if(structure.elementAt(i).startsWith("add_section")) {
                String sub = structure.elementAt(i).replace("add_section", "").trim();
                int section_index = Integer.parseInt(sub.split(" ")[0]);
                String section_name = sub.replace(""+section_index, "").trim();
                String file = blogSourcesDirectory + "/" + blog_id + "." + section_index;
                Vector<String> section = parseSection(file, section_index, section_name, blog_id);
                sections.add(section);

                String navigation_template = read(templatesDirectory+"/blog_navigation.template").elementAt(0);
                navigation_template = navigation_template.replace("{{ i-index }}", ""+section_index);
                navigation_template = navigation_template.replace("{{ section-name }}", section_name);
                navigation_structure.addElement(navigation_template);
            }
        }

        Vector<String> article = read(templatesDirectory+"/blog.template");
        article = replace(article, "{{ title }}", "CSIT Blog | "+blogName);
        article = replace(article, "{{ name }}", blogName);
        article = replace(article, "{{ author }}", blogAuthor);
        article = replace(article, "{{ date }}", blogPublishDate);

        for(String a : article) {
            if(a.trim().equals("{{ navigation_structure }}"))
                output = join(output, navigation_structure);
            else if(a.trim().equals("{{ sections }}"))
                for(Vector<String> section : sections)
                    output = join(output, section);
            else
                output.addElement(a);
        }

        return output;
    }

    public static Vector<String> parseSection(String file, int index, String name, String blog_id) {
        Vector<String> s = new Vector<>(1,1);
        Vector<String> sectionData = read(file);
        for(String data : sectionData) {
            if(data.startsWith("add_image")) {
                int imageIndex = Integer.parseInt(data.replace("add_image", "").trim());
                String imageStructureFile = blogSourcesDirectory + "/" + blog_id + ".img." + imageIndex;
                Vector<String> imageStructure = read(imageStructureFile);

                String imageAlignment = imageStructure.elementAt(0);
                String imageAsset = imageStructure.elementAt(1);
                String imageMaxWidth = imageStructure.elementAt(2);
                String imageCaption = imageStructure.elementAt(3);
                String imageSourceLink = imageStructure.elementAt(4);
                String imageSourceName = imageStructure.elementAt(5);

                Vector<String> imageCodeTemplate = read(templatesDirectory + "/blog_image.template");
                imageCodeTemplate = replace(imageCodeTemplate, "{{ image-alignment }}", imageAlignment);
                imageCodeTemplate = replace(imageCodeTemplate, "image-name", imageAsset);
                imageCodeTemplate = replace(imageCodeTemplate, "{{ image-size }}", imageMaxWidth);
                imageCodeTemplate = replace(imageCodeTemplate, "{{ image-caption }}", imageCaption);
                imageCodeTemplate = replace(imageCodeTemplate, "{{ image-source-link }}", imageSourceLink);
                imageCodeTemplate = replace(imageCodeTemplate, "{{ image-source-name }}", imageSourceName);

                s = join(s, imageCodeTemplate);
            } else s.addElement(data);
        }

        Vector<String> ns = new Vector<>(1,1);
        Vector<String> sectionTempData = read(templatesDirectory+"/blog_content.template");
        sectionTempData = replace(sectionTempData, "{{ section-index }}", ""+index);
        sectionTempData = replace(sectionTempData, "{{ section-name }}", name);
        for(String d : sectionTempData)
            if(d.trim().equals("{{ content }}"))
                ns = join(ns, s);
            else
                ns.addElement(d);
        return ns;
    }

    public static Vector<File> getBlogSources() {
        File folder = new File(blogSourcesDirectory);
        File[] listOfFiles = folder.listFiles();
        Vector<File> list = new Vector<>(1,1);
        for (File listOfFile : listOfFiles) if(listOfFile.getName().endsWith(".blog")) list.addElement(listOfFile);
        return list;
    }

    /* Template Management */
    public static Vector<File> getTemplateList() {
        File folder = new File(templatesDirectory);
        File[] listOfFiles = folder.listFiles();
        Vector<File> list = new Vector<>(1,1);
        for (File listOfFile : listOfFiles) list.addElement(listOfFile);
        return list;
    }

    public static Vector<String> processTemplates(Vector<String> source) {
        Vector<String> processed = new Vector<>(1,1);
        Vector<String> targets = new Vector<>(1,1);
        Vector<Vector<String>> data = new Vector<>(1,1);
        for(File temp : getTemplateList()) {
            String name = temp.getName().replace(".template", "");
            String target = "{{ include "+name+" }}";
            targets.addElement(target);
            data.addElement(read(temp.getPath()));
        }
        for(String st : source) {
            boolean skip = false;
            for(int i = 0; i < targets.size(); i++) {
                if(st.trim().contains(targets.elementAt(i))) {
                    for(String d : data.elementAt(i))
                        processed.addElement(d);
                    skip = true;
                }
            }
            if(!skip) processed.addElement(st);
        }
        return processed;
    }

    /* Asset Management */
    public static Vector<String[]> getAssetList() {
        File folder = new File(assetsDirectory);
        File[] listOfFiles = folder.listFiles();
        Vector<String[]> list = new Vector<>(1,1);
        for (File listOfFile : listOfFiles) {
            if(localBuild) list.addElement(new String[] {listOfFile.getName(), localhost+listOfFile.getPath()});
            else list.addElement(new String[] {listOfFile.getName(), deployHost+listOfFile.getPath()});
        }
        return list;
    }

    public static Vector<String> processAssetLinks(Vector<String> source, Vector<String[]> assets) {
        Vector<String> processed = source;
        for(String[] asset : assets) {
            String name = asset[0];
            String link = asset[1];
            processed = replace(processed, "{{ get_asset "+name+" }}", link);
        }
        return processed;
    }

    /* CrossRef Processor */
    public static Vector<String> processLinks(Vector<String> src, Vector<String> names, String prefix) {
        Vector<String> out = new Vector<>(1,1);
        for(String s : src) {
            for(String name : names)
                s = s.replace("{{ get_link "+name+" }}", "./"+name+".html");
            out.addElement(s);
        }
        return out;
    }

    public static void generateWarnings(Vector<String> source, String file) {
        for(int i = 0; i < source.size(); i++) {
            String src = source.elementAt(i);
            if(src.contains("{{") && src.contains("}}")) {
                if(src.contains("get_asset")) System.out.println("Warning ["+file+":"+(i+1)+"] : Found unprocessed dangling asset residue");
                else if(src.contains("get_link")) System.out.println("Warning ["+file+":"+(i+1)+"] : Found unresolved hyperlink residue");
                else if(src.contains("include")) System.out.println("Warning ["+file+":"+(i+1)+"] : Found unresolved template residue");
                else System.out.println("Warning ["+file+":"+(i+1)+"] : Found unknown residue");
            }
        }
    }

    public static Vector<String> read(String fn) {
        Vector<String> vec = new Vector<>(1,1);
        try {
            BufferedReader br = new BufferedReader(new FileReader(fn));
            while(true) {
                String s = br.readLine();
                if(s == null) break;
                vec.addElement(s);
            }
            br.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return vec;
    }

    public static void write(String fn, Vector<String> data) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fn));
            for(String st : data) bw.write(st + "\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void minwrite(String fn, Vector<String> data) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fn));
            for(String st : data) bw.write(st.trim());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Vector<String> replace(Vector<String> vector, String target, String replacement) {
        Vector<String> v = new Vector<>(1,1);
        for(int i = 0; i < vector.size(); i++) v.addElement(vector.elementAt(i).replace(target, replacement));
        return v;
    }

    public static Vector<String> join(Vector<String> v1, Vector<String> v2) {
        Vector<String> v = new Vector<>(1,1);
        for(String s : v1) v.addElement(s);
        for(String s : v2) v.addElement(s);
        return v;
    }

}