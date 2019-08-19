package Solution;

import java.util.Arrays;

public class StoryTesterImpl implements StoryTester {

    private boolean compareSroryAndAnottaion(String value, String story){
        String[] subSnt1Value = value.split("and ");
        String[] subSnt2Story = (value.split("or "))[0].split("and ");
        return false;
    }

    private String[][] removeLastWord(String[] lines){
        String[][] result;
        result = new String[lines.length][];
        for (int i =0 ; i < lines.length; i++){
            //result[i] =
        }

        return new String[0][];
    }

    @Override
    public void testOnInheritanceTree(String story, Class<?> testClass) throws Exception {

    }

    @Override
    public void testOnNestedClasses(String story, Class<?> testClass) throws Exception {

    }
}
