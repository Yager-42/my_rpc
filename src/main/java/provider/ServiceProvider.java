package provider;

public interface ServiceProvider {

    <T> void addServiceProvider(T service, String serviceName);
    public Object getServiceProvider(String serviceName);
}
