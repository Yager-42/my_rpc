package handler;

import annotation.Service;
import api.ByeService;

@Service
public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
