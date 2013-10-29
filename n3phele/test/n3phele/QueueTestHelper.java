package n3phele;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo.TaskStateInfo;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class QueueTestHelper {
	
	 public static void runAllTasksOnQueue() {		

		 LocalTaskQueue queue = LocalTaskQueueTestConfig.getLocalTaskQueue();
		 Map<String, QueueStateInfo> map = queue.getQueueStateInfo();

		 Set<String> keys = map.keySet();

		 for(String key : keys)
		 {
			 List<TaskStateInfo> tasksInfo = map.get(key).getTaskInfo();
			 for(TaskStateInfo taskInfo : tasksInfo)
			 {
				 String taskName = taskInfo.getTaskName();
				 queue.runTask(key, taskName);
			 }
		 }

		 map = queue.getQueueStateInfo();
		 keys = map.keySet();
		 for(String key : keys)
		 {
			 List<TaskStateInfo> tasksInfo = map.get(key).getTaskInfo();
			 if(tasksInfo.size() > 0)
			 {
				 runAllTasksOnQueue();				
			 }
		 }

	 }

}
