package provider;

public interface ServiceProvider {

    public <T> void addServiceProvider(T service);
    public Object getServiceProvider(String serviceName);
}
