package com.appdynamics.extensions.aws.ec2;

import com.appdynamics.extensions.conf.processor.ConfigProcessor;
import com.appdynamics.extensions.controller.ControllerClient;
import com.appdynamics.extensions.controller.ControllerClientFactory;
import com.appdynamics.extensions.controller.ControllerInfo;
import com.appdynamics.extensions.controller.ControllerInfoFactory;
import com.appdynamics.extensions.controller.ControllerInfoValidator;
import com.appdynamics.extensions.controller.apiservices.ControllerAPIService;
import com.appdynamics.extensions.controller.apiservices.ControllerAPIServiceFactory;
import com.appdynamics.extensions.controller.apiservices.MetricAPIService;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.util.JsonUtils;
import com.appdynamics.extensions.yml.YmlReader;
import com.google.common.collect.Maps;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Akshay Srivastava
 */
public class MetricCheckIT {

    private static final Logger logger = ExtensionsLoggerFactory.getLogger(MetricCheckIT.class);

    private static final String USER_AGENT = "Mozilla/5.0";

    private CloseableHttpClient httpClient;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private MetricAPIService metricAPIService;


    private static final String APPDYNAMICS_CONTROLLER_HOST_NAME_STRING = "APPDYNAMICS_CONTROLLER_HOST_NAME";
    private static final String APPDYNAMICS_CONTROLLER_PORT_STRING = "APPDYNAMICS_CONTROLLER_PORT";
    private static final String APPDYNAMICS_CONTROLLER_SSL_ENABLED_STRING = "APPDYNAMICS_CONTROLLER_SSL_ENABLED";
    private static final String EC2_INSTANCE_NAME_STRING = "EC2_INSTANCE_NAME";

    private static String CONTROLLER_HOST = null;
    private static String CONTROLLER_PORT = null;
    private static String CONTROLLER_SSL_ENABLED = null;
    private static String EC2_INSTANCE_NAME = null;
    private static final String ENCRYPTION_KEY = "encryptionKey";


    @Before
    public void setup() {

        Map<String, ?> config = YmlReader.readFromFileAsMap(new File("src/integration-test/resources/conf/config_ci.yml"));
        config = ConfigProcessor.process(config);
        Map controllerInfoMap = (Map) config.get("controllerInfo");
        if (controllerInfoMap == null) {
            controllerInfoMap = Maps.newHashMap();
        }
        //this is for test purposes only
        /*controllerInfoMap.put("controllerHost","localhost");
        controllerInfoMap.put(ENCRYPTION_KEY, config.get(ENCRYPTION_KEY));*/
        try {
            ControllerInfo controllerInfo = ControllerInfoFactory.initialize(controllerInfoMap, new File("src/integration-test/resources/conf/"));
            logger.info("Initialized ControllerInfo");
            ControllerInfoValidator controllerInfoValidator = new ControllerInfoValidator(controllerInfo);
            if (controllerInfoValidator.isValidated()) {
                ControllerClient controllerClient = ControllerClientFactory.initialize(controllerInfo,
                        (Map<String, ?>) config.get("connection"), (Map<String, ?>) config.get("proxy"),
                        (String) config.get(ENCRYPTION_KEY));
                logger.debug("Initialized ControllerClient");
                ControllerAPIService controllerAPIService = ControllerAPIServiceFactory.initialize(controllerInfo, controllerClient);
                metricAPIService = controllerAPIService.getMetricAPIService();
            }
        } catch (Exception ex) {
            logger.error("Failed to initialize the Controller API Service", ex);
        }

        /*CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials("admin@customer1", "admin");
        provider.setCredentials(AuthScope.ANY, credentials);

        httpClient = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();

        CONTROLLER_HOST = System.getenv(APPDYNAMICS_CONTROLLER_HOST_NAME_STRING);
        CONTROLLER_PORT = System.getenv(APPDYNAMICS_CONTROLLER_PORT_STRING);
        CONTROLLER_SSL_ENABLED = System.getenv(APPDYNAMICS_CONTROLLER_SSL_ENABLED_STRING);
        EC2_INSTANCE_NAME = System.getenv(EC2_INSTANCE_NAME_STRING);*/

    }

    @After
    public void tearDown() {
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAPICallsMetric() throws IOException {


        /*UrlBuilder builder = UrlBuilder.builder();
        builder.host(CONTROLLER_HOST).port(CONTROLLER_PORT).ssl(Boolean.valueOf(CONTROLLER_SSL_ENABLED)).path("controller/rest/applications/Server%20&%20Infrastructure%20Monitoring/metric-data");
        builder.query("metric-path", "Application%20Infrastructure%20Performance%7CRoot%7CCustom%20Metrics%7CAmazon%20EC2%7CAWS%20API%20Calls");
        builder.query("time-range-type", "BEFORE_NOW");
        builder.query("duration-in-mins", "1");
        builder.query("output", "JSON");*/

        JsonNode jsonNode = null;
        jsonNode = metricAPIService.getMetricData("", "Server%20&%20Infrastructure%20Monitoring/metric-data?metric-path=Application%20Infrastructure%20Performance%7CRoot%7CCustom%20Metrics%7CAmazon%7CEC2%20AWS%20API%20Calls&time-range-type=BEFORE_NOW&duration-in-mins=15&output=JSON");

        Assert.assertNotNull("Cannot connect to controller API", jsonNode);
        if (jsonNode != null) {
            JsonNode valueNode = JsonUtils.getNestedObject(jsonNode, "*", "metricValues", "*", "value");
            int awsAPICalls = (valueNode == null) ? 0 : valueNode.get(0).asInt();
            Assert.assertTrue("Invalid metric values", awsAPICalls >= 0);
        }

    }

    private CloseableHttpResponse sendGET(String url) throws IOException {

        HttpGet httpGet = new HttpGet(url);

        httpGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

        return httpResponse;
    }

}
