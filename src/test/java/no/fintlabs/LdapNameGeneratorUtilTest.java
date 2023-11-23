package no.fintlabs;

import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;

class LdapNameGeneratorUtilTest {

    @Test
    void generateNormal() {
        // Sett opp testdata
        String crdName = "drosjeloyve";
        String orgId = "agderfk.no";
        String type = "client";

        // Utfør metoden som skal testes
        String result = LdapNameGeneratorUtil.generate(crdName, orgId, type);

        // Sjekk at resultatet er som forventet
        assertEquals( "drosjeloyve", result.substring(0, result.length() - 6));
        System.out.println(result);
    }

    @Test
    void generateRemovalOfNo() {
        // Sett opp testdata
        String crdName = "drosjeloyve-beta-trondelagfylke-no";
        String orgId = "trondelagfylke.no";
        String type = "client";

        // Utfør metoden som skal testes
        String result = LdapNameGeneratorUtil.generate(crdName, orgId, type);

        // Sjekk at resultatet er som forventet
        assertEquals( "drosjeloyve-beta-trondelagfylke", result.substring(0, result.length() - 6));
        System.out.println(result);
    }

    @Test
    void generateRemovalOfFylke() {
        // Sett opp testdata
        String crdName = "drosjeloyve-beta-test-trondelagfylke-no";
        String orgId = "trondelagfylke.no";
        String type = "client";

        // Utfør metoden som skal testes
        String result = LdapNameGeneratorUtil.generate(crdName, orgId, type);

        // Sjekk at resultatet er som forventet
        assertEquals( "drosjeloyve-beta-test-trondelag", result.substring(0, result.length() - 6));
        System.out.println(result);
    }

    @Test
    void generateRemovalOfLongestPart() {
        // Sett opp testdata
        String crdName = "drosjeloyve-beta-trondelag-test1-t";
        String orgId = "trondelagfylke.no";
        String type = "client";

        // Utfør metoden som skal testes
        String result = LdapNameGeneratorUtil.generate(crdName, orgId, type);

        // Sjekk at resultatet er som forventet
        assertEquals( "drosjeloyv-beta-trondelag-test1-t", result.substring(0, result.length() - 6));
        System.out.println(result);
    }

    @Test
    void generateRemovalOfLongestPart2() {
        // Sett opp testdata
        String crdName = "drosjeloyve-beta-trondelag-test1-tes";
        String orgId = "trondelagfylke.no";
        String type = "client";

        // Utfør metoden som skal testes
        String result = LdapNameGeneratorUtil.generate(crdName, orgId, type);

        // Sjekk at resultatet er som forventet
        assertEquals( "drosjeloy-beta-trondela-test1-tes", result.substring(0, result.length() - 6));
        System.out.println(result);
    }

    @Test
    void generateRemovalOfLongestPart3() {
        // Sett opp testdata
        String crdName = "drosjeloyve-beta-trondelag-test1-tes-test";
        String orgId = "trondelagfylke.no";
        String type = "client";

        // Utfør metoden som skal testes
        String result = LdapNameGeneratorUtil.generate(crdName, orgId, type);

        // Sjekk at resultatet er som forventet
        assertEquals( "drosje-beta-tronde-test1-tes-test", result.substring(0, result.length() - 6));
        System.out.println(result);
    }

//    @Test
//    void generateLong() {
//        // Sett opp testdata
//        String crdName = "drosjeloyve-beta-trondelagfylke-no";
//        String randomString = "wayzk";
//        String orgId = "trondelagfylke.no";
//        String type = "client";
//
//        // Utfør metoden som skal testes
//        String result = LdapNameGeneratorUtil.generate(crdName, randomString, orgId, type);
//
//        // Sjekk at resultatet er som forventet
//        assertEquals("drosjeloyve-beta-trondelagfylke-no-wayzk@client.trondelagfylke.no", result);
//    }

    @Test
    void generateFullName() {
        // Sett opp testdata
        String crdName = "drosjeloyve-beta-trondelagfylke-no";
        String randomString = "wayzk";
        String orgId = "trondelagfylke.no";
        String type = "client";

        // Utfør metoden som skal testes
        String result = LdapNameGeneratorUtil.generateFullName(crdName, randomString, orgId, type);

        // Sjekk at resultatet er som forventet
        assertEquals("drosjeloyve-beta-trondelagfylke-no-wayzk@client.trondelagfylke.no", result);

    }
}