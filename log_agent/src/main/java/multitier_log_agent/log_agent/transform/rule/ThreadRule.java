package multitier_log_agent.log_agent.transform.rule;


import javassist.*;
import multitier_log_agent.log_agent.client.LogClientCodegen;
import multitier_log_agent.log_agent.transform.PointcutUtil;
import multitier_log_agent.log_agent.transform.TransformUtil;
import multitier_log_agent.log_agent.transform.instrumenter.CallInstrumenter;
import multitier_log_agent.log_shared.model.JoinpointInfo;
import org.apache.commons.configuration.HierarchicalConfiguration;

import multitier_log_agent.log_agent.query.IClassQuery;
import multitier_log_agent.log_agent.query.IMethodQuery;
import multitier_log_agent.log_agent.transform.TransformationRuleOrder;

/**
 * 未实现
 * @author admin
 *
 */
//public class ThreadRule extends MethodPointcutRule{
public class ThreadRule extends ThreadCallPointcutRule {

	private static final String Execute_Class = "java.util.concurrent.ThreadPoolExecutor";
	private static final String Execute_Name = Execute_Class + "." + "execute" + "(java.lang.Runnable)";
	private static final String Submit_Class = "java.util.concurrent.AbstractExecutorService";
	private static final String Submit_Names = "java.util.concurrent.AbstractExecutorService.submit"; //使用split分割

	private static final String Name="ThreadRule";
//	protected final List<String> include = new ArrayList<String>();
	public ThreadRule() {
		super(Name, TransformationRuleOrder.ThreadPointcut.ordinal());
	}
	public ThreadRule(HierarchicalConfiguration config) {
        this();
        logger.info("\tConfigure rule: " + getName());

//        for (HierarchicalConfiguration include : config.configurationsAt("include")) {
//            String val = include.getString("");
//            logger.info("\t\tAdd include: " + val);
//            addInclude(val);
//        }

        for (HierarchicalConfiguration region : config.configurationsAt("region")) {
            addRegion(region.getString(""));
        }
    }
//	public List<String> getInclude() {
//        return include;
//    }

//    public void addInclude(String glob) {
//        include.add(glob);
//    }

	@Override
	public boolean isClassIncluded(IClassQuery query) {
		String fullDotName = query.getFullDotName();
		return Execute_Class.equals(fullDotName) || Submit_Class.equals(fullDotName);
	}

	@Override
	public boolean isMethodIncluded(IMethodQuery query) {
		String fullDotName = query.getFullDotName();
		boolean res = Execute_Name.equals(fullDotName);
		if (res){
			return res;
		}
		res = Submit_Names.equals(fullDotName.split("\\(")[0]);
		return res;
	}

	@Override
	public boolean transform(CtClass cc) throws NotFoundException,
			CannotCompileException {
//		System.out.println("now ready threadpool:" + cc.getName());
//		if ("java.util.concurrent.ThreadPoolExecutor$Worker".equals(cc.getName())){
//			CtBehavior[] xx = cc.getDeclaredBehaviors();
//			for (int i = 1; i <= xx.length; i++){
//				System.out.println(i + xx[i-1].getLongName());
//			}
//		}
		//shangmianwei ceshi
		if (Execute_Class.equals(cc.getName())){
			for (CtBehavior method : cc.getDeclaredMethods()){
				if (method.getLongName().equals(Execute_Name)){
					//插装execut方法，相当于普通方法插装
					transform1(method, cc);
					//插装execute方法中的start方法，相当于call插装
					transform2(method);
				}
			}
			return true;
		}
		if (Submit_Class.equals(cc.getName())){
			for (CtBehavior method : cc.getDeclaredMethods()){
				if (Submit_Names.equals(method.getLongName().split("\\(")[0])){
					//插装submit系列的所有方法，相当于普通方法插装
					transform1(method, cc);
				}
			}
			return true;
		}
		return false;
	}

	protected void transform1(CtBehavior m, CtClass cc) throws CannotCompileException, NotFoundException {
		if (TransformUtil.isSystemExitProxy(m)) {
			// we won't instrument our 'custom added methods'
			return;
		}

		if (!PointcutUtil.isAbstract(m)) {

			logger.debug("\tInstrument on method '" + m.getLongName() + "'");

			String targetRef = "this";
			if (Modifier.isStatic(m.getModifiers())) {
				targetRef = null;
			}

			String recordEntry, recordExit;
			recordEntry = LogClientCodegen.recordJoinpoint(cc, m, targetRef, JoinpointInfo.EventType.CALL_THREAD,
					getRegionJavaString(), "pure",true, false);
			recordExit = LogClientCodegen.recordJoinpoint(cc, m, targetRef, JoinpointInfo.EventType.RETURN_THREAD,
					getRegionJavaString(),"pure", false, true);
//todo:这里将drug_pattern加了进来，可能会影响到正常运行
			if (m.isEmpty()) {
				// no body -> insert records as new body
				m.setBody("{" + recordEntry + " \n " + recordExit + "}");
			} else {
				// Handle normal call, and normal plus thrown exception return
				m.insertBefore(recordEntry);
				m.insertAfter(recordExit, true);

				// Handle system exit
				TransformUtil.addSystemExitProxy(m, recordExit);
			}
		}
	}


	protected void transform2(CtBehavior m) throws CannotCompileException {
		if (TransformUtil.isSystemExitProxy(m)) {
			// we won't instrument our 'custom added methods'
			return;
		}

		//System.out.println(m.getLongName());
		m.instrument(new CallInstrumenter(m, this));
	}


}
