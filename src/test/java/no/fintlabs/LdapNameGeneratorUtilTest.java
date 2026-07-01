package no.fintlabs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class LdapNameGeneratorUtilTest {
    @Test
    void generateNormal() {
        String crdName = "drosjeloyve";
        String orgId = "agderfk.no";
        String type = "client";

        String result = LdapNameGeneratorUtil.generate(crdName, orgId, type);

        Assertions.assertEquals( "drosjeloyve", result.substring(0, result.length() - 6));
        System.out.println(result);
    }

    @Test
    void generateRemovalOfNo() {
        String crdName = "drosjeloyve-beta-trondelagfylke-no";
        String orgId = "trondelagfylke.no";
        String type = "client";

        String result = LdapNameGeneratorUtil.generate(crdName, orgId, type);

        Assertions.assertEquals( "drosjeloyve-beta-trondelagfylke", result.substring(0, result.length() - 6));
        System.out.println(result);
    }

    @Test
    void generateRemovalOfFylke() {
        String crdName = "drosjeloyve-beta-test-trondelagfylke-no";
        String orgId = "trondelagfylke.no";
        String type = "client";

        String result = LdapNameGeneratorUtil.generate(crdName, orgId, type);

        Assertions.assertEquals( "drosjeloyve-beta-test-trondelag", result.substring(0, result.length() - 6));
        System.out.println(result);
    }

    @Test
    void generateRemovalOfLongestPart() {
        String crdName = "drosjeloyve-beta-trondelag-test1-t";
        String orgId = "trondelagfylke.no";
        String type = "client";

        String result = LdapNameGeneratorUtil.generate(crdName, orgId, type);

        Assertions.assertEquals( "drosjeloyv-beta-trondelag-test1-t", result.substring(0, result.length() - 6));
        System.out.println(result);
    }

    @Test
    void generateRemovalOfLongestPart2() {
        String crdName = "drosjeloyve-beta-trondelag-test1-tes";
        String orgId = "trondelagfylke.no";
        String type = "client";

        String result = LdapNameGeneratorUtil.generate(crdName, orgId, type);

        Assertions.assertEquals( "drosjeloy-beta-trondela-test1-tes", result.substring(0, result.length() - 6));
        System.out.println(result);
    }

    @Test
    void generateRemovalOfLongestPart3() {
        String crdName = "drosjeloyve-beta-trondelag-test1-tes-test";
        String orgId = "trondelagfylke.no";
        String type = "client";

        String result = LdapNameGeneratorUtil.generate(crdName, orgId, type);

        Assertions.assertEquals( "drosje-beta-tronde-test1-tes-test", result.substring(0, result.length() - 6));
        System.out.println(result);
    }

    @Test
    void generateFullName() {
        String crdName = "drosjeloyve-beta-trondelagfylke-no";
        String randomString = "wayzk";
        String orgId = "trondelagfylke.no";
        String type = "client";

        String result = LdapNameGeneratorUtil.generateFullName(crdName, randomString, orgId, type);

        Assertions.assertEquals("drosjeloyve-beta-trondelagfylke-no-wayzk@client.trondelagfylke.no", result);
        System.out.println(result);
    }
}
