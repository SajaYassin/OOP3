package Solution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
class Bclone implements Cloneable{
    public String b;
    @Override
    public Bclone clone(){
        Bclone bclone = new Bclone();
        bclone.b = "bclone is clonning!";
        return bclone;
    }

    public Bclone(){
        b = "Nothing just Nothing :(";
    }

    public Bclone(Bclone bclone){
        b = "bclone is coping :(";
    }

}

class Ccopy{
    public String c;

    public Ccopy(){
        c = "viod init :(";
    }

    public Ccopy(Ccopy saja) {
        c = "Copy Const is doning the job!";
    }

}

class Dnop{
    public String  nop;

    public Dnop(){
        nop ="NOOP !";
    }
}
class A{
    String string;
    public Bclone B;
    public Ccopy C;
    public Dnop D;
    public A(){
        B = new Bclone();
        C = new Ccopy();
        D = new Dnop();
        string = "init A";
    }
    @Given(value = "find Given &par and &par2")
    private void given(String s1,String s2){
        System.out.println(s1 + " " + s2);
        string = s1 + " " + s2;
    }
}

class D{
    String string;
    @Given(value = "run &par and &par2 and &par2")
    private void step1(String s1, String s2, String s3){
        string = s1 + " " + s2 + " " + s3;
    }
    @When("nothing")
    private void step2(){
        string += " step2 is done";
    }
    @Then("that happens &params")
    private void step3(String s3){
        System.out.println(string + " and " + s3 + " is Done!!");
        assertEquals(string + " and " + s3 + " is Done!!","step1 is done step2 is done and step3 is Done!!");
    }
}

public class StoryTesterImplTest {
    public StoryTesterImpl storyTester;

    @BeforeEach
    public void Init(){
        storyTester = new StoryTesterImpl();
    }

    @Test
    public void compareSroryAndAnottaionTest(){
        assertTrue(storyTester.compareSroryAndAnottaion("the number of students in the classroom is &size"
                ,"the number of students in the classroom is 60"));

        assertTrue(storyTester.compareSroryAndAnottaion("&size","90"));

        assertTrue(storyTester.compareSroryAndAnottaion("the &size","the 40"));

        assertTrue(storyTester.compareSroryAndAnottaion("the number of students in the classroom is &students and the number among them that are standing is &standing",
                "the number of students in the classroom is 80 and the number among them that are standing is 10"));

        assertTrue(storyTester.compareSroryAndAnottaion("the classroom is &condition and &condition"
                ,"the classroom is not-full and not-empty or the classroom is almost-full and not-empty"));

        assertTrue(storyTester.compareSroryAndAnottaion("",""));


    }

    @Test
    public void backupTest() throws Exception{
        A a = new A();
        A backed =(A) storyTester.backup(a,null);
        assertEquals(backed.B.b,"bclone is clonning!");
        assertEquals(backed.C.c,"Copy Const is doning the job!");
        assertEquals(backed.D.nop,"NOOP !");
        storyTester.restoreBackup(a,backed);
    }

    @Test
    public void testnotInherted() throws Exception{
//        storyTester.testOnInheritanceTree("Given find Given it and Works!!",A.class);
        storyTester.testOnInheritanceTree("Given run step1 and is and done\nWhen nothing\nThen that happens step3 or that happens step3 or that happens step5", D.class);
        assertThrows(GivenNotFoundException.class, () -> storyTester.testOnInheritanceTree("Given runs step1 and is and done\nWhen nothing\nThen that happens step3 or that happens step3 or that happens step5", D.class));
        assertThrows(WhenNotFoundException.class, () -> storyTester.testOnInheritanceTree("Given run step1 and is and done\nWhen ifjj vdgvdf\nThen that happens step3 ", D.class));
        assertThrows(ThenNotFoundException.class, () -> storyTester.testOnInheritanceTree("Given run step1 and is and done\nWhen nothing\nThen that happenss step3 ", D.class));
    }

}
