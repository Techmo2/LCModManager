package brad.tillmann;

import com.fasterxml.jackson.databind.util.StdConverter;
import de.skuzzle.semantic.Version;

public class StringToVersionConverter extends StdConverter<String, Version> {
    @Override
    public Version convert(String value) {
        return Version.parseVersion(value);
    }
}
