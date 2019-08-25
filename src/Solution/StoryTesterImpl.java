package Solution;
//import jdk.internal.org.objectweb.asm.commons.Method;

import org.junit.ComparisonFailure;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

public class StoryTesterImpl implements StoryTester {

    Object backup;
    boolean StartWhenSeq;
    public StoryTesterImpl(){
        backup = null;
        StartWhenSeq = true;
    }

    private boolean checkEquality(String[][] s1, String[][] s2) {
        if (s1 == s2)
            return true;

        if (s1 == null || s2 == null)
            return false;

        int n = s1.length;
        if (n != s2.length)
            return false;

        for (int i = 0; i < n; i++) {
            if (s1[i].length != s2[i].length)
                return false;

            for (int j = 0; j < s1[i].length; j++) {
                if (!s1[i][j].equals(s2[i][j]))
                    return false;
            }
        }

        return true;
    }

    public boolean compareSroryAndAnottaion(String value, String story) {
        String[][] valueLines = removeLastWord(value.split(" and "));
        String[][] storyLines = removeLastWord(story.split(" or ")[0].split(" and "));
        return checkEquality(valueLines, storyLines);
    }

    public String[][] removeLastWord(String[] lines) {
        String[][] result;
        result = new String[lines.length][];
        for (int i = 0; i < lines.length; i++) {
            String[] line = lines[i].split(" ");
            result[i] = Arrays.copyOf(line, line.length - 1);
        }
        return result;
    }


    public Object backup(Object old,Object parentObject) throws Exception {
        Class<?> c = old.getClass();
        Object theNew = createInstance(c,parentObject);
        Field[] fields = c.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            Set<String> interfaces = Arrays.stream(f.getType().getInterfaces()).map(i -> i.getName()).collect(Collectors.toSet());
            if (interfaces.contains("java.lang.Cloneable")) {
                Method clone = f.getType().getDeclaredMethod("clone");
                f.set(theNew, clone.invoke(f.get(old)));
            } else {
                try {
                    Constructor<?> constructor = f.getType().getDeclaredConstructor(f.getType());
                    f.set(theNew, constructor.newInstance(f.get(old)));
                } catch (NoSuchMethodException e) {
                    f.set(theNew, f.get(old));
                }
            }
        }
        return theNew;
    }

    public void restoreBackup(Object original, Object backup) throws IllegalAccessException {
        for (Field f : original.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            f.set(original, f.get(backup));
        }
    }

    public Class<? extends Annotation> getAnnotaionClass(String annotaionString) {
        if (annotaionString.equals("Given")) {
            return Given.class;
        } else if (annotaionString.equals("When")) {
            return When.class;
        } else {
            return Then.class;
        }
    }

    public void throwExp(String annotaionString) throws WordNotFoundException {
        if (annotaionString.equals("Given")) {
            throw new GivenNotFoundException();
        } else if (annotaionString.equals("When")) {
            throw new WhenNotFoundException();
        } else {
            throw new ThenNotFoundException();
        }
    }

    public String getValueFromAnnotation(Object annotation, String annotaionString) {
        if (annotaionString.equals("Given")) {
            return ((Given) annotation).value();
        } else if (annotaionString.equals("When")) {
            return ((When) annotation).value();
        } else {
            return ((Then) annotation).value();
        }
    }

    public Method findMethod(Class<?> testClass, String line) throws WordNotFoundException {
        String annotaionString = line.split(" ")[0];
        Class<? extends Annotation> annotaionClass = this.getAnnotaionClass(annotaionString);
        Class<?> thisClass = testClass;
        String newLine = line.replaceFirst(annotaionString + " ", "");
        while (true) {
            if (thisClass == null) this.throwExp(annotaionString);
            List<java.lang.reflect.Method> methods = Arrays.stream(thisClass.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(annotaionClass)
                            && compareSroryAndAnottaion(getValueFromAnnotation(m.getAnnotation(annotaionClass), annotaionString), newLine))
                    .collect(Collectors.toList());
            if (methods.size() == 0) {
                thisClass = thisClass.getSuperclass();
            } else {
                methods.get(0).setAccessible(true);
                return methods.get(0);
            }
        }
    }

    //get parameters from story subline II
    public Object[] getParametrs(Method method, String line) {
        List<String> parametersStr = Arrays.stream(line.split(" and ")).map(m -> m.split(" ")[m.split(" ").length - 1]).collect(Collectors.toList());
        Object[] result = new Object[method.getParameterCount()];
        Class<?>[] paramsTypes = method.getParameterTypes();
        for (int i = 0; i < method.getParameterCount(); i++) {
            if (paramsTypes[i].getSimpleName().equals("String")) {
                result[i] = parametersStr.get(i);
            } else {
                result[i] = Integer.valueOf(parametersStr.get(i));
            }
        }
        return result;
    }

    public void updateBackup(String storyLine, Object object, Object PaerntObj) throws Exception {
        if (storyLine.split(" ")[0].equals("When") && StartWhenSeq) {
            backup = this.backup(object, PaerntObj);
            StartWhenSeq = false;
        } else {
            if(!storyLine.split(" ")[0].equals("When")){
                StartWhenSeq = true;
            }
        }
    }
    @Override
    public void testOnInheritanceTree(String story, Class<?> testClass) throws Exception {
        testOnInheritanceTreeAux(story,testClass,null);
    }

    public Object createInstance(Class<?> testClass,Object parentObject)throws Exception{
        if(parentObject == null){
            return testClass.newInstance();
        }else{
            Constructor<?> ctor = testClass.getDeclaredConstructor(parentObject.getClass());
            return ctor.newInstance(parentObject);
        }
    }

    public void testOnInheritanceTreeAux(String story, Class<?> testClass, Object parentObject) throws Exception{
        if (story == null || testClass == null) throw new IllegalArgumentException();
        String[] storyLines = story.split("\n");
        Object object = createInstance(testClass,parentObject);
        StoryTestExceptionImpl exception = new StoryTestExceptionImpl();
        StartWhenSeq = true;
        for (String line : storyLines) {
            updateBackup(line,object, parentObject);
            Method method = this.findMethod(testClass, line);
            Vector<String> expected = new Vector<String>();
            Vector<String> actual = new Vector<String>();
            boolean thenSuccess = false;
            for (String subLine : line.split(" or ")) {
                try {
                    method.invoke(object, getParametrs(method, subLine));
                    thenSuccess = true;
                    break;
                } catch (InvocationTargetException e) {
                    ComparisonFailure failure = (ComparisonFailure) e.getTargetException();
                    expected.add(failure.getExpected());
                    actual.add(failure.getActual());
                }
            }
            if(!thenSuccess && line.split(" ")[0].equals("Then")){
                exception.update(line,expected,actual);
                restoreBackup(object,backup);
            }

        }
        if (exception.getNumFail() >  0) throw exception;
    }

    public void testOnNestedClassesAux(String story, Class<?> testClass, Object parentObj)throws Exception{
        if (story == null || testClass == null) throw new IllegalArgumentException();
        try {
            testOnInheritanceTreeAux(story,testClass,parentObj);
            return;
        }catch (GivenNotFoundException e){
            Class<?>[] innerClasses = testClass.getDeclaredClasses();
            Object instance = createInstance(testClass,parentObj);
            for(Class<?> innerClass : innerClasses){
                if(innerClass.isInterface()) continue;
                try {
                    testOnNestedClassesAux(story, innerClass, instance);
                    return;
                }catch (GivenNotFoundException e1) { }
            }
        }
        throw new GivenNotFoundException();
    }

    @Override
    public void testOnNestedClasses(String story, Class<?> testClass) throws Exception {
        testOnNestedClassesAux(story,testClass,null);
    }
}
