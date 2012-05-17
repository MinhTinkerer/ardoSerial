package eu.amaxilatis.codebender;

import eu.amaxilatis.codebender.actions.FlashPrivilegedAction;
import eu.amaxilatis.codebender.graphics.ArduinoStatusImage;
import eu.amaxilatis.codebender.graphics.PortOutputViewerFrame;
import eu.amaxilatis.codebender.util.SerialPortList;
import jssc.SerialNativeInterface;
import org.apache.log4j.BasicConfigurator;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

/**
 * A JApplet class.
 * Provides user interface to connecto to an arduino using a usb connection.
 */
public class CodeBenderApplet extends JApplet {
    /**
     * Logger.
     */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(CodeBenderApplet.class);

    private static SerialNativeInterface serialInterface = new SerialNativeInterface();

    /**
     * a connection handler.
     */
    private Thread serialPortThread;
    private final String[] rates = new String[12];
    private String[] detectedPorts;
    private String[] ports;
    private boolean started = false;
    public static String version;
    public static String buildNum;


    @Override
    public final void destroy() {
        LOGGER.info("CodeBenderApplet called Destroy");
        ConnectionManager.getInstance().disconnect();
    }

    /**
     * default constructor.
     *
     * @throws HeadlessException an exception.
     */
    public CodeBenderApplet() {
        BasicConfigurator.configure();
        Properties properties = new Properties();
        try {

            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("/props/version.properties"));
            version = (String) properties.get("version");
            buildNum = (String) properties.get("build");
            LOGGER.info("Version:" + version);
            LOGGER.info("Build:" + buildNum);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    /**
     * Initialize the baudrates array.
     */
    private void initBaudRates() {

    }

    public String getRates() {
        return rates.toString();
    }

    public String getFireRates() {
        return ConnectionManager.getInstance().getBaudrates();
    }

    @Override
    public final void init() {
        LOGGER.info("CodeBenderApplet called Init");
    }

    /**
     * Build the default user interface.
     */
    private void createGUI() {
        LOGGER.info("CodeBenderApplet called CreateGUI");

        this.setBackground(Color.white);

        ArduinoStatusImage.setDisconnected();

        LOGGER.info("booting up");
    }

    /**
     * Called from javascript.
     *
     * @return a comma separated list of all available usb ports.
     */
    public String getFire2() {
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            ports = SerialPortList.getInstance().getPortNames();
//                            LOGGER.info(ports);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InvocationTargetException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return 0;
            }
        });

        final StringBuilder protsAvail = new StringBuilder();
//        LOGGER.info(ports);
        for (int i = 0; i < ports.length; i++) {
            protsAvail.append(",");
            protsAvail.append(ports[i]);

        }
        return (protsAvail.toString()).substring(1);
    }

    /**
     * Override connect function to be used by javascript.
     *
     * @param port the index of the port to connect to.
     * @param rate the rate to use when connecting.
     */
    public void overrideConnect(final int port, final int rate) {
        ConnectionManager.getInstance().setjTextArea(new PortOutputViewerFrame());

        ConnectionManager.getInstance().setPort(ports[port], rate);
        ConnectionManager.getInstance().connect();
    }

    public void flash(final int port, final String filename, final String baudrate) {

        AccessController.doPrivileged(new FlashPrivilegedAction(ports[port], filename, baudrate));
    }

}