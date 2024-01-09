package Giver.service;

import annotation.RpcService;
import api.ByeService;

@RpcService
public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
