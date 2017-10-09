package uia.utils.states;

import org.junit.Assert;
import org.junit.Test;

import uia.utils.states.StateMachine.RunResultType;

public class AbstractStateWorkerTest extends AbstractStateWorker<AbstractStateWorkerTest, Object> {

    private static String IDLE = "IDLE";

    private static String PRE_PROCESS = "PRE_PROCESS";

    private static String PROCESSING = "PROCESSING";

    private static String POST_PROCESS = "POST_PROCESS";

    private static String NEXT = "NEXT";

    private static String RUN_HOLD = "RUN_HOLD";

    private int seqNo;

    public AbstractStateWorkerTest() {
        super("TEST");
    }

    @Test
    public void testNormal() {
        printlnHeader();

        addEventListener("moveIn", a -> {
            Assert.assertEquals(4, this.seqNo);
        });
        addStateInListener(PRE_PROCESS, a -> {
            Assert.assertEquals(4, this.seqNo);
        });
        addStateOutListener(PRE_PROCESS, a -> {
            Assert.assertEquals(5, this.seqNo);
        });
        addStateChangedListener(PROCESSING, POST_PROCESS, a -> {
            Assert.assertEquals(6, this.seqNo);
        });

        RunResultType rt;

        changeState(IDLE);
        Assert.assertEquals(IDLE, getCurrState());

        this.seqNo++;
        rt = run("???", null);
        Assert.assertEquals(RunResultType.EVENT_NOT_SUPPORT, rt);
        println("???", null);

        this.seqNo++;
        rt = run("validateLot", null);
        Assert.assertEquals(RunResultType.STATE_KEEP, rt);
        Assert.assertEquals(IDLE, getCurrState());
        println("validateLot", null);

        this.seqNo++;
        rt = run("trackIn", null);
        Assert.assertEquals(RunResultType.EVENT_NOT_SUPPORT, rt);
        Assert.assertEquals(IDLE, getCurrState());
        println("trackIn", null);

        this.seqNo++;
        rt = run("moveIn", null);
        Assert.assertEquals(RunResultType.STATE_CHANGED, rt);
        Assert.assertEquals(PRE_PROCESS, getCurrState());
        println("moveIn", null);

        this.seqNo++;
        rt = run("trackIn", null);
        Assert.assertEquals(RunResultType.STATE_CHANGED, rt);
        Assert.assertEquals(PROCESSING, getCurrState());
        println("trackIn", null);

        this.seqNo++;
        rt = run("trackOut", null);
        Assert.assertEquals(RunResultType.STATE_CHANGED, rt);
        Assert.assertEquals(POST_PROCESS, getCurrState());
        println("trackOut", null);

        this.seqNo++;
        rt = run("moveOut", null);
        Assert.assertEquals(RunResultType.STATE_CHANGED, rt);
        Assert.assertEquals(NEXT, getCurrState());
        println("moveOut", null);

        this.seqNo++;
        rt = run("ready", null);
        Assert.assertEquals(RunResultType.STATE_CHANGED, rt);
        Assert.assertEquals(IDLE, getCurrState());
        println("ready", null);
    }

    @Override
    protected void initial() {
        AbstractStateWorker.PRINTABLE = true;

        // IDLE
        this.stateMachine.register(IDLE)
                .addEvent("validateLot", AbstractStateWorkerTest.this::validateLot)
                .addEvent("moveIn", AbstractStateWorkerTest.this::moveIn);

        // PRE_PROCESS
        this.stateMachine.register(PRE_PROCESS)
                .addEvent("trackIn", AbstractStateWorkerTest.this::trackIn)
                .addEvent("down", AbstractStateWorkerTest.this::runHold)
                .addEvent("trackOut", AbstractStateWorkerTest.this::trackIn);

        // PROCESSING
        this.stateMachine.register(PROCESSING)
                .addEvent("trackIn", AbstractStateWorkerTest.this::trackIn)
                .addEvent("down", AbstractStateWorkerTest.this::runHold)
                .addEvent("trackOut", AbstractStateWorkerTest.this::trackOut);

        // POST_PROCESS
        this.stateMachine.register(POST_PROCESS)
                .addEvent("down", AbstractStateWorkerTest.this::runHold)
                .addEvent("moveOut", AbstractStateWorkerTest.this::moveOut);

        // NEXT
        this.stateMachine.register(NEXT)
                .addEvent("ready", AbstractStateWorkerTest.this::ready);

        // RUN_HOLD
        this.stateMachine.register(RUN_HOLD);
    }

    private RunResultType run(String eventName, Object args) {
        return this.stateMachine.run(this, eventName, args);
    }

    private String validateLot(AbstractStateWorker<AbstractStateWorkerTest, Object> controller, Object args) {
        return null;
    }

    private String moveIn(AbstractStateWorker<AbstractStateWorkerTest, Object> controller, Object args) {
        return PRE_PROCESS;
    }

    private String moveOut(AbstractStateWorker<AbstractStateWorkerTest, Object> controller, Object args) {
        return NEXT;
    }

    private String trackIn(AbstractStateWorker<AbstractStateWorkerTest, Object> controller, Object args) {
        return PROCESSING;
    }

    private String trackOut(AbstractStateWorker<AbstractStateWorkerTest, Object> controller, Object args) {
        return POST_PROCESS;
    }

    private String runHold(AbstractStateWorker<AbstractStateWorkerTest, Object> controller, Object args) {
        return RUN_HOLD;
    }

    private String ready(AbstractStateWorker<AbstractStateWorkerTest, Object> controller, Object args) {
        return IDLE;
    }

}
