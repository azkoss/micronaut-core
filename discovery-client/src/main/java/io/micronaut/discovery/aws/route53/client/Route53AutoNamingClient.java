package io.micronaut.discovery.aws.route53.client;

import com.amazonaws.services.servicediscovery.AWSServiceDiscovery;
import com.amazonaws.services.servicediscovery.AWSServiceDiscoveryClient;
import com.amazonaws.services.servicediscovery.model.*;
import io.micronaut.configurations.aws.AWSClientConfiguration;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.discovery.DiscoveryClient;
import io.micronaut.discovery.ServiceInstance;
import io.micronaut.discovery.aws.route53.Route53ClientDiscoveryConfiguration;
import io.micronaut.discovery.aws.route53.Route53DiscoveryConfiguration;
import io.micronaut.http.client.Client;
import org.reactivestreams.Publisher;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


@Client(id = Route53ClientDiscoveryConfiguration.SERVICE_ID, path = "/", configuration = Route53ClientDiscoveryConfiguration.class)
@Requires(beans = Route53DiscoveryConfiguration.class)
@Requires(beans = AWSClientConfiguration.class)
@Requires(property = "aws.route53.discovery.enabled", value = "true", defaultValue = "false")
public class Route53AutoNamingClient implements DiscoveryClient {

    AWSClientConfiguration awsClientConfiguration;

    Route53ClientDiscoveryConfiguration route53ClientDiscoveryConfiguration;

    Route53DiscoveryConfiguration route53DiscoveryConfiguration;

    AWSServiceDiscovery discoveryClient;


    @Override
    public String getDescription() {
        return null;
    }


    @Override
    public Publisher<List<ServiceInstance>> getInstances(String serviceId) {
        if (discoveryClient==null) {
            discoveryClient = AWSServiceDiscoveryClient.builder().withClientConfiguration(awsClientConfiguration.clientConfiguration).build();
        }
        if (serviceId==null) {
            serviceId = route53ClientDiscoveryConfiguration.getAwsServiceId();  // we can default to the config file
        }

        ListInstancesRequest instancesRequest = new ListInstancesRequest().withServiceId(serviceId);
        ListInstancesResult instanceResult = discoveryClient.listInstances(instancesRequest);
        List<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();
        for (InstanceSummary instanceSummary : instanceResult.getInstances()) {
            try {
                ServiceInstance serviceInstance = ServiceInstance.builder(instanceSummary.getId(),new URI((String)instanceSummary.getAttributes().get("URI"))).build();
                serviceInstances.add(serviceInstance);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return Publishers.just(
                serviceInstances
        );

    }

    @Override
    public Publisher<List<String>> getServiceIds() {

        AWSServiceDiscovery client = AWSServiceDiscoveryClient.builder().build();
        ServiceFilter serviceFilter = new ServiceFilter().withName("NAMESPACE_ID").withValues(route53ClientDiscoveryConfiguration.getNamespaceId());
        ListServicesRequest listServicesRequest = new ListServicesRequest().withFilters(serviceFilter);
        ListServicesResult response = client.listServices(listServicesRequest);
        List<ServiceSummary> services = response.getServices();
        List<String> serviceIds = new ArrayList<String>();
        for (ServiceSummary service : services) {
            serviceIds.add(service.getId());
        }
        return Publishers.just(
                serviceIds
        );
    }

    @Override
    public void close() throws IOException {
        discoveryClient.shutdown();
    }
}
