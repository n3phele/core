package n3phele.service.model;
import javax.xml.bind.annotation.XmlEnum;

/**
 * 
 * @author Cristina Scheibler
 *
 */
@XmlEnum(String.class)
public enum VirtualServerStatus {
	running, initializing, debug, terminated, pending
}
