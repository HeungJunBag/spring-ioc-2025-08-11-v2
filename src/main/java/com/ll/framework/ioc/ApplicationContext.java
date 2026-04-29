package com.ll.framework.ioc;

import com.ll.framework.ioc.annotations.Component;
import com.ll.standard.util.Ut;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Constructor;
import java.util.Set;

public class ApplicationContext {
    private final String basePackage;

    public ApplicationContext(String basePackage) {
        this.basePackage = basePackage;
    }

    public void init() {
        // 아직 비어있음
    }

    @SuppressWarnings("unchecked")
    public <T> T genBean(String beanName) {
        // ① com.ll 아래 @Component 계열 클래스를 전부 스캔
        Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Component.class);

        for (Class<?> clazz : classes) {
            // ② 클래스 이름 → 빈 이름으로 변환해서 요청한 이름과 비교
            if (Ut.str.lcfirst(clazz.getSimpleName()).equals(beanName)) {
                try {
                    // ③ 리플렉션으로 객체 생성 (파라미터는 일단 null)
                    Constructor<?> c = clazz.getDeclaredConstructors()[0];
                    c.setAccessible(true);
                    Object[] args = new Object[c.getParameterTypes().length];
                    return (T) c.newInstance(args);
                } catch (Exception e) {
                    throw new RuntimeException("빈 생성 실패: " + beanName, e);
                }
            }
        }
        return null;
    }
}
