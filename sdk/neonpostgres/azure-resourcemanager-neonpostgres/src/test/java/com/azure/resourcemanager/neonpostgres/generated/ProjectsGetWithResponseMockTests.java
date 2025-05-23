// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.neonpostgres.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.models.AzureCloud;
import com.azure.core.test.http.MockHttpResponse;
import com.azure.resourcemanager.neonpostgres.NeonPostgresManager;
import com.azure.resourcemanager.neonpostgres.models.EndpointType;
import com.azure.resourcemanager.neonpostgres.models.Project;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public final class ProjectsGetWithResponseMockTests {
    @Test
    public void testGetWithResponse() throws Exception {
        String responseStr
            = "{\"properties\":{\"entityId\":\"l\",\"entityName\":\"lctzeyowmndcovd\",\"createdAt\":\"qauxzan\",\"provisioningState\":\"Canceled\",\"attributes\":[{\"name\":\"ruw\",\"value\":\"udrbcpftxudqyem\"},{\"name\":\"bunaucmcirtn\",\"value\":\"emmjauwcg\"}],\"regionId\":\"fnohaitran\",\"storage\":7588587614389235236,\"pgVersion\":1040664060,\"historyRetention\":1553463796,\"defaultEndpointSettings\":{\"autoscalingLimitMinCu\":42.55249926263288,\"autoscalingLimitMaxCu\":27.41029291177378},\"branch\":{\"entityId\":\"fbngfcocef\",\"entityName\":\"riylfmpztra\",\"createdAt\":\"svhl\",\"provisioningState\":\"Failed\",\"attributes\":[{\"name\":\"regp\",\"value\":\"tmojhvrztnvgyshq\"},{\"name\":\"dgrtwmew\",\"value\":\"zlpykcfazzwjcay\"}],\"projectId\":\"zrransyb\",\"parentId\":\"polwzrghsrlei\",\"roleName\":\"fscjfn\",\"databaseName\":\"wvuagfqwtl\",\"roles\":[{\"entityId\":\"m\",\"entityName\":\"uptrklz\",\"createdAt\":\"jajwolxfsvag\",\"provisioningState\":\"Canceled\",\"attributes\":[{\"name\":\"wl\",\"value\":\"lr\"},{\"name\":\"igt\",\"value\":\"jcvbxqlapsns\"},{\"name\":\"ovyxpavidnie\",\"value\":\"wffcvvye\"},{\"name\":\"slpuxgcbdsva\",\"value\":\"pnptw\"}],\"branchId\":\"kx\",\"permissions\":[\"zwugxyqvnjobfel\"],\"isSuperUser\":true},{\"entityId\":\"uhzzgqlmfaew\",\"entityName\":\"iudjp\",\"createdAt\":\"qhttqhnmhkre\",\"provisioningState\":\"Failed\",\"attributes\":[{\"name\":\"xheq\",\"value\":\"gcruxspinym\"}],\"branchId\":\"gwokmikpazfbmjxu\",\"permissions\":[\"pfdvhaxdv\",\"zaehpphthd\",\"lmv\",\"tatlakfqoi\"],\"isSuperUser\":true},{\"entityId\":\"ksbbvtooxrp\",\"entityName\":\"wp\",\"createdAt\":\"hgjtnhtukfaci\",\"provisioningState\":\"Canceled\",\"attributes\":[{\"name\":\"tumeezbxvqxb\",\"value\":\"uvwc\"},{\"name\":\"asgom\",\"value\":\"mjzwx\"},{\"name\":\"qgo\",\"value\":\"sxpwwztjfmkkh\"}],\"branchId\":\"fred\",\"permissions\":[\"cgrllc\",\"na\",\"vjowazhpabac\"],\"isSuperUser\":false},{\"entityId\":\"otgkwsxnsrqorcg\",\"entityName\":\"mv\",\"createdAt\":\"bxeetqujxcxxqn\",\"provisioningState\":\"Canceled\",\"attributes\":[{\"name\":\"e\",\"value\":\"wqurc\"},{\"name\":\"ojmrvvxwjongzse\",\"value\":\"qqrsil\"},{\"name\":\"chskxxka\",\"value\":\"sbvr\"},{\"name\":\"aqgvto\",\"value\":\"rulfuct\"}],\"branchId\":\"rthcfjzh\",\"permissions\":[\"ubqjro\",\"tvrjeqmtz\"],\"isSuperUser\":true}],\"databases\":[{\"entityId\":\"trx\",\"entityName\":\"xrd\",\"createdAt\":\"bsrwrsnrhpqat\",\"provisioningState\":\"Succeeded\",\"attributes\":[{\"name\":\"yanxkvvcs\",\"value\":\"msvuvdjkqxetq\"},{\"name\":\"mlivrjjxnwx\",\"value\":\"chp\"},{\"name\":\"jxlehzlx\",\"value\":\"gfquwz\"},{\"name\":\"w\",\"value\":\"ibelwcerwkw\"}],\"branchId\":\"jxljtxbusq\",\"ownerName\":\"xxniuisdzhgbd\"},{\"entityId\":\"pagsecnad\",\"entityName\":\"wqrgxfllmqi\",\"createdAt\":\"ezoell\",\"provisioningState\":\"Failed\",\"attributes\":[{\"name\":\"w\",\"value\":\"mtum\"},{\"name\":\"pymdjfuax\",\"value\":\"oqvqpilr\"},{\"name\":\"uncanlduwzorx\",\"value\":\"bm\"},{\"name\":\"aqklxy\",\"value\":\"x\"}],\"branchId\":\"vfqepd\",\"ownerName\":\"ltuubw\"},{\"entityId\":\"pjbowcpj\",\"entityName\":\"uqgixex\",\"createdAt\":\"dfbwljav\",\"provisioningState\":\"Succeeded\",\"attributes\":[{\"name\":\"jddvrgliegftc\",\"value\":\"biiftksdwgdnk\"}],\"branchId\":\"gmwdh\",\"ownerName\":\"buvczldbglzoutb\"},{\"entityId\":\"qgz\",\"entityName\":\"ajclyzgsnorbjg\",\"createdAt\":\"zjotvmrxkhlo\",\"provisioningState\":\"Failed\",\"attributes\":[{\"name\":\"hvhd\",\"value\":\"qayfl\"},{\"name\":\"iyu\",\"value\":\"snuudtelvhyibdr\"},{\"name\":\"rswhbuubpyro\",\"value\":\"tjoxztfw\"}],\"branchId\":\"chvczev\",\"ownerName\":\"nctagfyvrtpqpem\"}],\"endpoints\":[{\"entityId\":\"krepdqhqyhwqwem\",\"entityName\":\"qabckmzeoxin\",\"createdAt\":\"re\",\"provisioningState\":\"Canceled\",\"attributes\":[{\"name\":\"lpuzjpcee\",\"value\":\"nzangprbfaxy\"}],\"projectId\":\"lbciphmsexro\",\"branchId\":\"ndktxfv\",\"endpointType\":\"read_write\"},{\"entityId\":\"eqg\",\"entityName\":\"rietbg\",\"createdAt\":\"xx\",\"provisioningState\":\"Canceled\",\"attributes\":[{\"name\":\"yfwnw\",\"value\":\"iwxeiicrmpep\"},{\"name\":\"ldmaxxijvskwsdgk\",\"value\":\"gyacwrasekwef\"},{\"name\":\"voinwo\",\"value\":\"artwyxqic\"},{\"name\":\"advatdavuqmcb\",\"value\":\"msfobjlquvj\"}],\"projectId\":\"cjumv\",\"branchId\":\"imioyo\",\"endpointType\":\"read_only\"},{\"entityId\":\"miqwnnrac\",\"entityName\":\"bbfqpspklady\",\"createdAt\":\"nhautwukexzgpmnm\",\"provisioningState\":\"Succeeded\",\"attributes\":[{\"name\":\"qilwgdfpfqfpcvs\",\"value\":\"clg\"},{\"name\":\"rvwerfwxbsmtb\",\"value\":\"jj\"},{\"name\":\"h\",\"value\":\"ci\"}],\"projectId\":\"wdv\",\"branchId\":\"brekqhsqhtf\",\"endpointType\":\"read_only\"}]},\"roles\":[{\"entityId\":\"ejuwyqwdqigmghgi\",\"entityName\":\"txlujkhnjcmr\",\"createdAt\":\"fmkhcqtwmlmhjnqt\",\"provisioningState\":\"Failed\",\"attributes\":[{\"name\":\"e\",\"value\":\"vragpokddx\"}],\"branchId\":\"hhkvguavtptbk\",\"permissions\":[\"qynspgbvoffb\",\"kwvdxa\",\"xqokmyrlji\"],\"isSuperUser\":false},{\"entityId\":\"nobrqlpb\",\"entityName\":\"trpzuyudivbxnh\",\"createdAt\":\"eaeonqelwgdh\",\"provisioningState\":\"Canceled\",\"attributes\":[{\"name\":\"tzarogatmoljiy\",\"value\":\"mpinmzvfkneerzzt\"}],\"branchId\":\"nsjulugdybnh\",\"permissions\":[\"elf\",\"hkeizcp\",\"htdm\",\"wjekptycaydbj\"],\"isSuperUser\":false},{\"entityId\":\"mlcfnzhmhsurl\",\"entityName\":\"qkpmmzpstau\",\"createdAt\":\"awi\",\"provisioningState\":\"Canceled\",\"attributes\":[{\"name\":\"sgvvjhvvlrl\",\"value\":\"hewjjmajnkdflqio\"},{\"name\":\"swaeqkzfz\",\"value\":\"xjoshohtotryegpk\"},{\"name\":\"xrmexznlw\",\"value\":\"bfokxkhu\"},{\"name\":\"ze\",\"value\":\"ufgjblcdr\"}],\"branchId\":\"fcemftzgyyky\",\"permissions\":[\"gekdfqnht\",\"wdowrczfjjn\"],\"isSuperUser\":false},{\"entityId\":\"rkkmhmn\",\"entityName\":\"wempdcifrhju\",\"createdAt\":\"sulwzpflusn\",\"provisioningState\":\"Failed\",\"attributes\":[{\"name\":\"gzotfriyrgkoekvz\",\"value\":\"xxyxhighctxbxm\"}],\"branchId\":\"pcqydeykvsk\",\"permissions\":[\"dfrjeizik\",\"qaboohxbms\",\"ycqsxr\"],\"isSuperUser\":true}],\"databases\":[{\"entityId\":\"yqaeohpjhgejk\",\"entityName\":\"hhdau\",\"createdAt\":\"hoo\",\"provisioningState\":\"Failed\",\"attributes\":[{\"name\":\"k\",\"value\":\"xjxjoe\"},{\"name\":\"lqxr\",\"value\":\"dknkobe\"}],\"branchId\":\"mbozom\",\"ownerName\":\"amicbigwcdgz\"},{\"entityId\":\"znuxkeuairaabmdl\",\"entityName\":\"bedpfixlhupmomih\",\"createdAt\":\"dnpxpkcdpr\",\"provisioningState\":\"Succeeded\",\"attributes\":[{\"name\":\"yi\",\"value\":\"ghflrufssjyghsf\"},{\"name\":\"rkbhammgmqfm\",\"value\":\"fgvqcpdw\"},{\"name\":\"gquxweysland\",\"value\":\"dcdjhunh\"}],\"branchId\":\"cgawnrrnquo\",\"ownerName\":\"otire\"}],\"endpoints\":[{\"entityId\":\"obfsxstc\",\"entityName\":\"lbvzm\",\"createdAt\":\"cjzlquzexokjxebj\",\"provisioningState\":\"Succeeded\",\"attributes\":[{\"name\":\"zabwmvog\",\"value\":\"jsvlpg\"}],\"projectId\":\"nwcehaqidoyzl\",\"branchId\":\"iomqoqpepiaea\",\"endpointType\":\"read_only\"}]},\"id\":\"rgdtpeqnacyheqw\",\"name\":\"pqqncju\",\"type\":\"khjoz\"}";

        HttpClient httpClient
            = response -> Mono.just(new MockHttpResponse(response, 200, responseStr.getBytes(StandardCharsets.UTF_8)));
        NeonPostgresManager manager = NeonPostgresManager.configure()
            .withHttpClient(httpClient)
            .authenticate(tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                new AzureProfile("", "", AzureCloud.AZURE_PUBLIC_CLOUD));

        Project response
            = manager.projects().getWithResponse("q", "fut", "dpvozglqj", com.azure.core.util.Context.NONE).getValue();

        Assertions.assertEquals("lctzeyowmndcovd", response.properties().entityName());
        Assertions.assertEquals("ruw", response.properties().attributes().get(0).name());
        Assertions.assertEquals("udrbcpftxudqyem", response.properties().attributes().get(0).value());
        Assertions.assertEquals("fnohaitran", response.properties().regionId());
        Assertions.assertEquals(7588587614389235236L, response.properties().storage());
        Assertions.assertEquals(1040664060, response.properties().pgVersion());
        Assertions.assertEquals(1553463796, response.properties().historyRetention());
        Assertions.assertEquals(42.55249926263288,
            response.properties().defaultEndpointSettings().autoscalingLimitMinCu());
        Assertions.assertEquals(27.41029291177378,
            response.properties().defaultEndpointSettings().autoscalingLimitMaxCu());
        Assertions.assertEquals("riylfmpztra", response.properties().branch().entityName());
        Assertions.assertEquals("regp", response.properties().branch().attributes().get(0).name());
        Assertions.assertEquals("tmojhvrztnvgyshq", response.properties().branch().attributes().get(0).value());
        Assertions.assertEquals("zrransyb", response.properties().branch().projectId());
        Assertions.assertEquals("polwzrghsrlei", response.properties().branch().parentId());
        Assertions.assertEquals("fscjfn", response.properties().branch().roleName());
        Assertions.assertEquals("wvuagfqwtl", response.properties().branch().databaseName());
        Assertions.assertEquals("uptrklz", response.properties().branch().roles().get(0).entityName());
        Assertions.assertEquals("wl", response.properties().branch().roles().get(0).attributes().get(0).name());
        Assertions.assertEquals("lr", response.properties().branch().roles().get(0).attributes().get(0).value());
        Assertions.assertEquals("kx", response.properties().branch().roles().get(0).branchId());
        Assertions.assertEquals("zwugxyqvnjobfel", response.properties().branch().roles().get(0).permissions().get(0));
        Assertions.assertTrue(response.properties().branch().roles().get(0).isSuperUser());
        Assertions.assertEquals("xrd", response.properties().branch().databases().get(0).entityName());
        Assertions.assertEquals("yanxkvvcs",
            response.properties().branch().databases().get(0).attributes().get(0).name());
        Assertions.assertEquals("msvuvdjkqxetq",
            response.properties().branch().databases().get(0).attributes().get(0).value());
        Assertions.assertEquals("jxljtxbusq", response.properties().branch().databases().get(0).branchId());
        Assertions.assertEquals("xxniuisdzhgbd", response.properties().branch().databases().get(0).ownerName());
        Assertions.assertEquals("qabckmzeoxin", response.properties().branch().endpoints().get(0).entityName());
        Assertions.assertEquals("lpuzjpcee",
            response.properties().branch().endpoints().get(0).attributes().get(0).name());
        Assertions.assertEquals("nzangprbfaxy",
            response.properties().branch().endpoints().get(0).attributes().get(0).value());
        Assertions.assertEquals("lbciphmsexro", response.properties().branch().endpoints().get(0).projectId());
        Assertions.assertEquals("ndktxfv", response.properties().branch().endpoints().get(0).branchId());
        Assertions.assertEquals(EndpointType.READ_WRITE,
            response.properties().branch().endpoints().get(0).endpointType());
        Assertions.assertEquals("txlujkhnjcmr", response.properties().roles().get(0).entityName());
        Assertions.assertEquals("e", response.properties().roles().get(0).attributes().get(0).name());
        Assertions.assertEquals("vragpokddx", response.properties().roles().get(0).attributes().get(0).value());
        Assertions.assertEquals("hhkvguavtptbk", response.properties().roles().get(0).branchId());
        Assertions.assertEquals("qynspgbvoffb", response.properties().roles().get(0).permissions().get(0));
        Assertions.assertFalse(response.properties().roles().get(0).isSuperUser());
        Assertions.assertEquals("hhdau", response.properties().databases().get(0).entityName());
        Assertions.assertEquals("k", response.properties().databases().get(0).attributes().get(0).name());
        Assertions.assertEquals("xjxjoe", response.properties().databases().get(0).attributes().get(0).value());
        Assertions.assertEquals("mbozom", response.properties().databases().get(0).branchId());
        Assertions.assertEquals("amicbigwcdgz", response.properties().databases().get(0).ownerName());
        Assertions.assertEquals("lbvzm", response.properties().endpoints().get(0).entityName());
        Assertions.assertEquals("zabwmvog", response.properties().endpoints().get(0).attributes().get(0).name());
        Assertions.assertEquals("jsvlpg", response.properties().endpoints().get(0).attributes().get(0).value());
        Assertions.assertEquals("nwcehaqidoyzl", response.properties().endpoints().get(0).projectId());
        Assertions.assertEquals("iomqoqpepiaea", response.properties().endpoints().get(0).branchId());
        Assertions.assertEquals(EndpointType.READ_ONLY, response.properties().endpoints().get(0).endpointType());
    }
}
