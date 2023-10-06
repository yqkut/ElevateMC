package dev.apposed.prime.proxy.module;

import java.util.HashMap;
import java.util.Map;

public class ModuleHandler {

    private Map<Class<? extends Module>, Module> modules;

    public ModuleHandler() {
        this.modules = new HashMap<>();
    }

    public void registerModule(Module module) {
        this.modules.put(module.getClass(), module);
        module.onEnable();
    }

    public void disableModule(Module module) {
        this.modules.remove(module.getClass());
        module.onDisable();
    }

    public void disableModule(Class<? extends Module> module) {
        this.disableModule(getModule(module));
    }

    public void disableModules() {
        this.modules.values().forEach(Module::onDisable);
        this.modules.clear();
    }

    public<T extends Module> T getModule(Class<? extends T> clazz) {
        return clazz.cast(this.modules.get(clazz));
    }
}