package uia.utils.states;

import org.junit.Assert;
import org.junit.Test;

public class StateMachineTest {

    private static String IDLE = "IDLE";

    private static String PRE_PROCESS = "PRE_PROCESS";

    private static String PROCESSING = "PROCESSING";

    private static String POST_PROCESS = "POST_PROCESS";

    private static String NEXT = "NEXT";

    private static String RUN_HOLD = "RUN_HOLD";

    private int event;

    @Test
    public void testSimple() {
        StateMachine<Object, Object> machine = new StateMachine<Object, Object>("FOUP");
        // IDLE
        machine.register(IDLE)
                .addEvent("validateLot", StateMachineTest.this::validateLot)
                .addEvent("moveIn", StateMachineTest.this::moveIn);

        // PRE_PROCESS
        machine.register(PRE_PROCESS)
                .addEvent("trackIn", StateMachineTest.this::trackIn)
                .addEvent("down", StateMachineTest.this::runHold)
                .addEvent("trackOut", StateMachineTest.this::trackIn);

        // PROCESSING
        machine.register(PROCESSING)
                .addEvent("trackIn", StateMachineTest.this::trackIn)
                .addEvent("down", StateMachineTest.this::runHold)
                .addEvent("trackOut", StateMachineTest.this::trackOut);

        // POST_PROCESS
        machine.register(POST_PROCESS)
                .addEvent("down", StateMachineTest.this::runHold)
                .addEvent("moveOut", StateMachineTest.this::moveOut);

        // NEXT
        machine.register(NEXT)
                .addEvent("ready", StateMachineTest.this::ready);

        Assert.assertEquals("FOUP", machine.getName());
        Assert.assertNotNull(machine.getState(IDLE));
        Assert.assertNotNull(machine.getState(PRE_PROCESS));
        Assert.assertNotNull(machine.getState(PROCESSING));
        Assert.assertNotNull(machine.getState(POST_PROCESS));
        Assert.assertNotNull(machine.getState(NEXT));
        Assert.assertNull(machine.getState(RUN_HOLD));

        machine.changeState(IDLE);
        machine.println();
        Assert.assertEquals("NULL", machine.getPrevState().getName());
        Assert.assertEquals(IDLE, machine.getCurrState().getName());

        machine.run(null, "validateLot", null);
        machine.println();
        Assert.assertEquals("NULL", machine.getPrevState().getName());
        Assert.assertEquals(IDLE, machine.getCurrState().getName());

        machine.run(null, "trackIn", null); // NO CHANGED
        machine.println();
        Assert.assertEquals("NULL", machine.getPrevState().getName());
        Assert.assertEquals(IDLE, machine.getCurrState().getName());

        machine.run(null, "moveIn", null);
        machine.println();
        Assert.assertEquals(IDLE, machine.getPrevState().getName());
        Assert.assertEquals(PRE_PROCESS, machine.getCurrState().getName());

        machine.run(null, "trackIn", null);
        machine.println();
        Assert.assertEquals(PRE_PROCESS, machine.getPrevState().getName());
        Assert.assertEquals(PROCESSING, machine.getCurrState().getName());

        machine.run(null, "trackOut", null);
        machine.println();
        Assert.assertEquals(PROCESSING, machine.getPrevState().getName());
        Assert.assertEquals(POST_PROCESS, machine.getCurrState().getName());

        machine.run(null, "moveOut", null);
        machine.println();
        Assert.assertEquals(POST_PROCESS, machine.getPrevState().getName());
        Assert.assertEquals(NEXT, machine.getCurrState().getName());

        machine.run(null, "ready", null);
        machine.println();
        Assert.assertEquals(NEXT, machine.getPrevState().getName());
        Assert.assertEquals(IDLE, machine.getCurrState().getName());
    }

    @Test
    public void testEventListener() {
        StateMachine<Object, Object> machine = new StateMachine<Object, Object>("FOUP");
        // IDLE
        machine.register(IDLE)
                .addEvent("validateLot", StateMachineTest.this::validateLot)
                .addEvent("moveIn", StateMachineTest.this::moveIn);

        // PRE_PROCESS
        machine.register(PRE_PROCESS)
                .addEvent("trackIn", StateMachineTest.this::trackIn)
                .addEvent("down", StateMachineTest.this::runHold)
                .addEvent("trackOut", StateMachineTest.this::trackIn);

        // PROCESSING
        machine.register(PROCESSING)
                .addEvent("trackIn", StateMachineTest.this::trackIn)
                .addEvent("down", StateMachineTest.this::runHold)
                .addEvent("trackOut", StateMachineTest.this::trackOut);

        // POST_PROCESS
        machine.register(POST_PROCESS)
                .addEvent("down", StateMachineTest.this::runHold)
                .addEvent("moveOut", StateMachineTest.this::moveOut);

        // NEXT
        machine.register(NEXT)
                .addEvent("ready", StateMachineTest.this::ready);

        // RUN_HOLD
        machine.register(RUN_HOLD);

        // EVENT LISTENERS
        machine.addChangeListener(IDLE, PRE_PROCESS, a -> Assert.assertEquals(2, this.event));
        machine.addEventListener("trackIn", a -> Assert.assertEquals(3, this.event));
        machine.addEventListener("moveOut", a -> Assert.assertEquals(5, this.event));

        machine.changeState(IDLE);
        Assert.assertEquals(IDLE, machine.getCurrState().getName());

        this.event = 1;
        machine.run(null, "validateLot", null);
        Assert.assertEquals(IDLE, machine.getCurrState().getName());

        this.event = 2;
        machine.run(null, "moveIn", null);
        Assert.assertEquals(PRE_PROCESS, machine.getCurrState().getName());

        this.event = 3;
        machine.run(null, "trackIn", null);
        Assert.assertEquals(PROCESSING, machine.getCurrState().getName());

        this.event = 4;
        machine.run(null, "trackOut", null);
        Assert.assertEquals(POST_PROCESS, machine.getCurrState().getName());

        this.event = 5;
        machine.run(null, "moveOut", null);
        Assert.assertEquals(NEXT, machine.getCurrState().getName());

        this.event = 6;
        machine.run(null, "ready", null);
        Assert.assertEquals(IDLE, machine.getCurrState().getName());
    }

    @Test
    public void testEx() {
        try {
            StateMachine<Object, Object> machine = new StateMachine<Object, Object>("FOUP");
            machine.changeState("unknown");
        }
        catch (StateException ex) {
            Assert.assertEquals("unknown", ex.eventName);
            Assert.assertEquals("Event:unknown not found in StateMachine:FOUP", ex.getMessage());

        }
    }

    private String validateLot(Object controller, Object args) {
        return null;
    }

    private String moveIn(Object controller, Object args) {
        return PRE_PROCESS;
    }

    private String moveOut(Object controller, Object args) {
        return NEXT;
    }

    private String trackIn(Object controller, Object args) {
        return PROCESSING;
    }

    private String trackOut(Object controller, Object args) {
        return POST_PROCESS;
    }

    private String runHold(Object controller, Object args) {
        return RUN_HOLD;
    }

    private String ready(Object controller, Object args) {
        return IDLE;
    }
}