package com.epochong; /**
 * @author epochong
 * @date 2019/7/13 9:30
 * @email epochong@163.com
 * @blog epochong.github.io
 * @describe
 */

import com.epochong.annotation.Benchmark;
import com.epochong.annotation.Measurement;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 运行类
 */
class CaseRunner {
    private final List<Case> caseList;
    private final int DEFAULT_ITERATIONS = 10;
    private final int DEFAULT_GROUP = 5;
    public CaseRunner(List<Case> caseList) {
        this.caseList = caseList;
    }

    //每组实验都要预热
    public void run() throws InvocationTargetException, IllegalAccessException {
        for (Case bCase : caseList
             ) {
            int iterations = DEFAULT_ITERATIONS;
            int group = DEFAULT_GROUP;
            //先获取类级别配置
            Measurement classMeasurement = bCase.getClass().getAnnotation(Measurement.class);
            if (classMeasurement != null) {
                iterations = classMeasurement.iterations();
                group = classMeasurement.group();
            }
            //找到对象中哪些方法是需要测试的方法
            Method[] methods = bCase.getClass().getMethods();
            for (Method method : methods
                 ) {
                Benchmark benchmark = method.getAnnotation(Benchmark.class);
                if (benchmark == null) {
                    continue;
                }
                Measurement methodMeasurement = method.getAnnotation(Measurement.class);
                if (methodMeasurement != null) {
                    iterations = methodMeasurement.iterations();
                    group = methodMeasurement.group();
                }
                runCase(bCase,method,iterations,group);
            }
        }
    }

    private void runCase(Case bCase, Method method, int iterations, int group) throws InvocationTargetException, IllegalAccessException {
        System.out.println(method.getName());
        for (int i = 0; i < group; i++) {
            System.out.print("第" + (i + 1) + "次实验," );
            long t1 = System.nanoTime();
            for (int j = 0; j < iterations; j++) {
                method.invoke(bCase);
            }
            long t2 = System.nanoTime();
            System.out.println("耗时：" + (t2 - t1));

        }
    }
}

/**
 * 加载类
 */
public class CaseLoader {

    public CaseRunner load() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String pkg = "com/epochong/cases";
        String pkgDot = "com.epochong.cases";
        List<String> classNameList = new ArrayList <String>();
        /*
        * 1.根据一个固定类，找到类加载器
        * 2.根据加载器找到类文件所在路径
        * 3.扫描路径的所有类文件
        * */
        ClassLoader classLoader = this.getClass().getClassLoader();
        Enumeration<URL> urls = classLoader.getResources(pkg);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if ((!url.getProtocol().equals("file"))) {
                //如果不是*.class文件，暂时不支持（查查如何从jar包下加载）
                continue;
            }
            String dirname = URLDecoder.decode(url.getPath(),"UTF-8");
            File dir = new File(dirname);
            if (!dir.isDirectory()) {
                continue;
            }
            File[] files = dir.listFiles();
            if (files == null) {
                continue;
            }
            for (File file : files) {
                String filename = file.getName();
                String className = filename.substring(0,filename.length() - 6);
                classNameList.add(className);
            }
        }
        List<Case> caseList = new ArrayList <Case>();
        for (String className : classNameList
             ) {
            Class<?>  cls = Class.forName(pkgDot + "." + className);
            if (hasInterface(cls,Case.class)) {
                caseList.add(((Case)cls.newInstance()));
            }
        }
        return new CaseRunner(caseList);
    }
    private boolean hasInterface(Class<?> cls, Class<?> intf) {
        Class<?>[] intfs = cls.getClasses();
        for (Class<?> i : intfs
             ) {
            if (i == intf) {
                return true;
            }
        }
        return false;
    }
}
