package multitier_log_agent.log_agent.transform;

/**
 * 
 * @author mleemans
 *
 * The order in which transformation rules are applied matters.
 * This is due to how the bytecode callstack is modified during transformations.
 * This enumeration specifies the correct order for different transformation types
 * through the enumeration ordinal values.
 */
public enum TransformationRuleOrder {
    // First method body inspection
    ThreadPointcut,
    MethodCallPointcut,
    // Then the rest
    MethodPointcut,
    InterfacePointcut,
    Servlet,
    Socket,
    SocketChannel;
}
