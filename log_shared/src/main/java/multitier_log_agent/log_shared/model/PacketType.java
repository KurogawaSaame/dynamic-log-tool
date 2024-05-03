package multitier_log_agent.log_shared.model;

public enum PacketType {
    NewCase,
    Joinpoint,
    CommJoinpoint,
    CallJoinpoint,
    ExceptionJoinpoint,
    ThreadJoinpoint;
}
