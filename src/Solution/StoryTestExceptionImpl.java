package Solution;

import java.util.List;
import java.util.Vector;
public class StoryTestExceptionImpl extends StoryTestException{
    private String firstFailedThen;
    private List<String> expected;
    private List<String> result;
    private int numFail;
    public StoryTestExceptionImpl(){

        numFail = 0;
    }

    public void update(String thenline,List<String> Expected,List<String> Result){
        if(numFail == 0){
            expected = new Vector<String>(Expected);
            result = new Vector<String>(Result);
            numFail = 1;
            firstFailedThen = new String(thenline);
        } else {
            numFail += 1;
        }
    }
    @Override
    public String getSentence() {
        return firstFailedThen;
    }

    @Override
    public List<String> getStoryExpected() {
        return expected;
    }

    @Override
    public List<String> getTestResult() {
        return result;
    }

    @Override
    public int getNumFail() {
        return numFail;
    }
}

