package no.fintlabs;

import org.apache.commons.lang3.RandomStringUtils;

public class LdapNameGeneratorUtil {

    public static final int RANDOM_CHARS = 5;
    public static final int MAX_LDAP_CHARS = 64;

    public static String generate(String crdName, String orgId, String type) {

        String randomString = RandomStringUtils.randomAlphabetic(RANDOM_CHARS).toLowerCase();

        if (getFullNameLength(crdName, randomString, orgId, type) > MAX_LDAP_CHARS && crdName.contains("-no-")) {
            crdName = crdName.replace("-no-", "-");
        }

        if (getFullNameLength(crdName, randomString, orgId, type) > MAX_LDAP_CHARS && crdName.endsWith("-no")) {
            crdName = crdName.substring(0, crdName.length() - 3);
        }

        if (getFullNameLength(crdName, randomString, orgId, type) > MAX_LDAP_CHARS && crdName.contains("fylke")) {
            crdName = crdName.replace("fylke", "");
        }

        while (getFullNameLength(crdName, randomString, orgId, type) > MAX_LDAP_CHARS) {
            crdName = removeCharacterFromLongestPart(crdName);
        }

        return String.format("%s-%s", crdName, randomString);
    }

    private static String removeCharacterFromLongestPart(String crdName) {
        String[] parts = crdName.split("-");
        int longestPartIndex = -1;
        int longestPartLength = 0;

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].length() >= longestPartLength) {
                longestPartLength = parts[i].length();
                longestPartIndex = i;
            }
        }

        if (longestPartIndex != -1 && parts[longestPartIndex].length() > 0) {
            parts[longestPartIndex] = parts[longestPartIndex].substring(0, parts[longestPartIndex].length() - 1);
        }

        return String.join("-", parts);
    }

    public static int getFullNameLength(String crdName, String randomString, String orgId, String type) {
        return generateFullName(crdName, randomString, orgId, type).length();
    }

    public static String generateFullName(String crdName, String randomString, String orgId, String type) {
        return String.format("%s-%s@%s.%s", crdName, randomString, type, orgId);
    }

}
