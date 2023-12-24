package brad.tillmann;


import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.beanutils.BeanUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try
        {
            run();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            PersistentConfig.getInstance().flush();
        }
    }

    public static void run() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, IOException {
        LethalCompanyMod bepInEx = LethalCompanyModCatalog.getInstance().search("bepinexpack").stream().findFirst().orElseThrow();

        List<String> fields = List.of("name", "fullName", "owner", "packageUrl", "uuid");
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        for(String field: fields)
            sb.append(String.format("'%s' = '%s'\n", field, BeanUtils.getProperty(bepInEx, field)));

        sb.append("\nVersions:\n");
        List<LethalCompanyModVersion> modVersions = bepInEx.getVersions();
        for(LethalCompanyModVersion modVersion: modVersions)
            sb.append(String.format("\t%s\n", modVersion.getVersion().toString()));

        System.out.println(sb);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());
        String asJson = objectWriter.writeValueAsString(bepInEx);
        Files.writeString(Paths.get("C:\\Users\\bradly\\Desktop\\BepInEx_ModDescriptor.json"), asJson, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }
}