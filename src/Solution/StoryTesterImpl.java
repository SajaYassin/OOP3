package Solution;
import java.util.Arrays;

public class StoryTesterImpl implements StoryTester {

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
// i didn't test this part
    private boolean compareSroryAndAnottaion(String value, String story){
        String[][] valueLines = removeLastWord(value.split("and "));
        String[][] storyLines = removeLastWord(value.split("or ")[0].split("and "));
        return checkEquality(valueLines,storyLines);
    }

    private String[][] removeLastWord(String[] lines){
        String[][] result;
        result = new String[lines.length][];
        for (int i =0 ; i < lines.length; i++){
            String[] line = lines[i].split(" ");
            result[i] = Arrays.copyOf(line,line.length -1);
        }
        return result;
    }

    @Override
    public void testOnInheritanceTree(String story, Class<?> testClass) throws Exception {

    }

    @Override
    public void testOnNestedClasses(String story, Class<?> testClass) throws Exception {

    }
}
