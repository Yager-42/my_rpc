package provider;

public interface ServiceProvider {

    <T> void addServiceProvider(T service, Class<T> serviceClass);
    public Object getServiceProvider(String serviceName);
}
