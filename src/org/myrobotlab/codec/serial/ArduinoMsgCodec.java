package org.myrobotlab.codec.serial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.myrobotlab.logging.LoggingFactory;
import org.myrobotlab.service.Arduino;
import org.myrobotlab.service.Runtime;
import org.myrobotlab.service.Serial;
import org.myrobotlab.service.interfaces.LoggingSink;
import org.python.netty.handler.codec.CodecException;
import org.slf4j.Logger;

// FIXME - use InputStream OutputStream 
// Stream encoders are more complicated than Document 
// with InputStream decoding - you need to deal with blocking / timeouts etc
// if the thing before it deals with it then you have a byte array - but it may not be complete

/**
 * Codec to interface with the Arduino service and MRLComm.ino part of this file
 * is dynamically generated from the method signatures of the Arduino service
 * 
 * MAGIC_NUMBER|NUM_BYTES|FUNCTION|DATA0|DATA1|....|DATA(N) NUM_BYTES - is the
 * number of bytes after NUM_BYTES to the end
 * 
 * @author GroG
 *
 */
public class ArduinoMsgCodec extends Codec implements Serializable {

  public ArduinoMsgCodec() {
    super(null);
  }

  public ArduinoMsgCodec(LoggingSink sink) {
    super(sink);
  }

  private static final long serialVersionUID = 1L;

  public final static Logger log = LoggerFactory.getLogger(ArduinoMsgCodec.class);

  transient static final HashMap<Integer, String> byteToMethod = new HashMap<Integer, String>();
  transient static final HashMap<String, Integer> methodToByte = new HashMap<String, Integer>();
  int byteCount = 0;
  int decodeMsgSize = 0;
  StringBuilder rest = new StringBuilder();

  public static final int MAX_MSG_SIZE = 64;

  public static final int MRLCOMM_VERSION = 34;

  public static final int MAGIC_NUMBER = 170; // 10101010

  // ----------- event types -------------------
  public static final int STEPPER_EVENT_STOP = 1;
  public static final int STEPPER_EVENT_STEP = 2;

  ///// java ByteToMethod generated definition - DO NOT MODIFY - Begin //////
  // {publishMRLCommError Integer}
  public final static int PUBLISH_MRLCOMM_ERROR = 1;

  // {getVersion}
  public final static int GET_VERSION = 2;

  // {publishVersion Integer}
  public final static int PUBLISH_VERSION = 3;

  // {analogReadPollingStart Integer}
  public final static int ANALOG_READ_POLLING_START = 4;

  // {analogReadPollingStop Integer}
  public final static int ANALOG_READ_POLLING_STOP = 5;

  // {analogWrite Integer Integer}
  public final static int ANALOG_WRITE = 6;

  // {digitalReadPollingStart Integer}
  public final static int DIGITAL_READ_POLLING_START = 7;

  // {digitalReadPollingStop Integer}
  public final static int DIGITAL_READ_POLLING_STOP = 8;

  // {digitalWrite Integer Integer}
  public final static int DIGITAL_WRITE = 9;

  // {motorAttach Motor}
  public final static int MOTOR_ATTACH = 10;

  // {motorDetach Motor}
  public final static int MOTOR_DETACH = 11;

  // {motorMove Motor}
  public final static int MOTOR_MOVE = 12;

  // {motorMoveTo Motor}
  public final static int MOTOR_MOVE_TO = 13;

  // {motorReset Motor}
  public final static int MOTOR_RESET = 14;

  // {motorStop Motor}
  public final static int MOTOR_STOP = 15;

  // {pinMode Integer Integer}
  public final static int PIN_MODE = 16;

  // {publishCustomMsg Object[]}
  public final static int PUBLISH_CUSTOM_MSG = 17;

  // {publishLoadTimingEvent Long}
  public final static int PUBLISH_LOAD_TIMING_EVENT = 18;

  // {publishPin Pin}
  public final static int PUBLISH_PIN = 19;

  // {publishPulse Long}
  public final static int PUBLISH_PULSE = 20;

  // {publishPulseStop Integer}
  public final static int PUBLISH_PULSE_STOP = 21;

  // {publishSensorData Object}
  public final static int PUBLISH_SENSOR_DATA = 22;

  // {publishServoEvent Integer}
  public final static int PUBLISH_SERVO_EVENT = 23;

  // {publishTrigger Pin}
  public final static int PUBLISH_TRIGGER = 24;

  // {pulse int int int int}
  public final static int PULSE = 25;

  // {pulseStop}
  public final static int PULSE_STOP = 26;

  // {sensorAttach SensorDataSink}
  public final static int SENSOR_ATTACH = 27;

  // {sensorPollingStart String int}
  public final static int SENSOR_POLLING_START = 28;

  // {sensorPollingStop String}
  public final static int SENSOR_POLLING_STOP = 29;

  // {servoAttach Servo Integer}
  public final static int SERVO_ATTACH = 30;

  // {servoDetach Servo}
  public final static int SERVO_DETACH = 31;

  // {servoEventsEnabled Servo}
  public final static int SERVO_EVENTS_ENABLED = 32;

  // {servoSweepStart Servo}
  public final static int SERVO_SWEEP_START = 33;

  // {servoSweepStop Servo}
  public final static int SERVO_SWEEP_STOP = 34;

  // {servoWrite Servo}
  public final static int SERVO_WRITE = 35;

  // {servoWriteMicroseconds Servo}
  public final static int SERVO_WRITE_MICROSECONDS = 36;

  // {setDebounce int}
  public final static int SET_DEBOUNCE = 37;

  // {setDigitalTriggerOnly Boolean}
  public final static int SET_DIGITAL_TRIGGER_ONLY = 38;

  // {setLoadTimingEnabled boolean}
  public final static int SET_LOAD_TIMING_ENABLED = 39;

  // {setPWMFrequency Integer Integer}
  public final static int SET_PWMFREQUENCY = 40;

  // {setSampleRate int}
  public final static int SET_SAMPLE_RATE = 41;

  // {setSerialRate int}
  public final static int SET_SERIAL_RATE = 42;

  // {setServoSpeed Servo}
  public final static int SET_SERVO_SPEED = 43;

  // {setTrigger int int int}
  public final static int SET_TRIGGER = 44;

  // {softReset}
  public final static int SOFT_RESET = 45;
  
  //{debugEnable}
  public final static int DEBUG_ENABLE = 46;

  //{debugDisable}
  public final static int DEBUG_DISABLE = 47;
  
  // {messageAck} coming back from arduino
  public final static int PUBLISH_MESSAGE_ACK = 127;
  
  // {publishDebug} a debug string returned from the arduino.
  public final static int PUBLISH_DEBUG = 126;

  // Sensor Types:
  public final static int SENSOR_TYPE_DIGITAL_PIN = 1;
  public final static int SENSOR_TYPE_ANALOG_PIN  = 3;
  
  
  
  static {
    byteToMethod.put(PUBLISH_MRLCOMM_ERROR, "publishMRLCommError");
    methodToByte.put("publishMRLCommError", PUBLISH_MRLCOMM_ERROR);

    byteToMethod.put(GET_VERSION, "getVersion");
    methodToByte.put("getVersion", GET_VERSION);

    byteToMethod.put(PUBLISH_VERSION, "publishVersion");
    methodToByte.put("publishVersion", PUBLISH_VERSION);

    byteToMethod.put(ANALOG_READ_POLLING_START, "analogReadPollingStart");
    methodToByte.put("analogReadPollingStart", ANALOG_READ_POLLING_START);

    byteToMethod.put(ANALOG_READ_POLLING_STOP, "analogReadPollingStop");
    methodToByte.put("analogReadPollingStop", ANALOG_READ_POLLING_STOP);

    byteToMethod.put(ANALOG_WRITE, "analogWrite");
    methodToByte.put("analogWrite", ANALOG_WRITE);

    byteToMethod.put(DIGITAL_READ_POLLING_START, "digitalReadPollingStart");
    methodToByte.put("digitalReadPollingStart", DIGITAL_READ_POLLING_START);

    byteToMethod.put(DIGITAL_READ_POLLING_STOP, "digitalReadPollingStop");
    methodToByte.put("digitalReadPollingStop", DIGITAL_READ_POLLING_STOP);

    byteToMethod.put(DIGITAL_WRITE, "digitalWrite");
    methodToByte.put("digitalWrite", DIGITAL_WRITE);

    byteToMethod.put(MOTOR_ATTACH, "motorAttach");
    methodToByte.put("motorAttach", MOTOR_ATTACH);

    byteToMethod.put(MOTOR_DETACH, "motorDetach");
    methodToByte.put("motorDetach", MOTOR_DETACH);

    byteToMethod.put(MOTOR_MOVE, "motorMove");
    methodToByte.put("motorMove", MOTOR_MOVE);

    byteToMethod.put(MOTOR_MOVE_TO, "motorMoveTo");
    methodToByte.put("motorMoveTo", MOTOR_MOVE_TO);

    byteToMethod.put(MOTOR_RESET, "motorReset");
    methodToByte.put("motorReset", MOTOR_RESET);

    byteToMethod.put(MOTOR_STOP, "motorStop");
    methodToByte.put("motorStop", MOTOR_STOP);

    byteToMethod.put(PIN_MODE, "pinMode");
    methodToByte.put("pinMode", PIN_MODE);

    byteToMethod.put(PUBLISH_CUSTOM_MSG, "publishCustomMsg");
    methodToByte.put("publishCustomMsg", PUBLISH_CUSTOM_MSG);

    byteToMethod.put(PUBLISH_LOAD_TIMING_EVENT, "publishLoadTimingEvent");
    methodToByte.put("publishLoadTimingEvent", PUBLISH_LOAD_TIMING_EVENT);

    byteToMethod.put(PUBLISH_PIN, "publishPin");
    methodToByte.put("publishPin", PUBLISH_PIN);

    byteToMethod.put(PUBLISH_PULSE, "publishPulse");
    methodToByte.put("publishPulse", PUBLISH_PULSE);

    byteToMethod.put(PUBLISH_PULSE_STOP, "publishPulseStop");
    methodToByte.put("publishPulseStop", PUBLISH_PULSE_STOP);

    byteToMethod.put(PUBLISH_SENSOR_DATA, "publishSensorData");
    methodToByte.put("publishSensorData", PUBLISH_SENSOR_DATA);

    byteToMethod.put(PUBLISH_SERVO_EVENT, "publishServoEvent");
    methodToByte.put("publishServoEvent", PUBLISH_SERVO_EVENT);

    byteToMethod.put(PUBLISH_TRIGGER, "publishTrigger");
    methodToByte.put("publishTrigger", PUBLISH_TRIGGER);

    byteToMethod.put(PULSE, "pulse");
    methodToByte.put("pulse", PULSE);

    byteToMethod.put(PULSE_STOP, "pulseStop");
    methodToByte.put("pulseStop", PULSE_STOP);

    byteToMethod.put(SENSOR_ATTACH, "sensorAttach");
    methodToByte.put("sensorAttach", SENSOR_ATTACH);

    byteToMethod.put(SENSOR_POLLING_START, "sensorPollingStart");
    methodToByte.put("sensorPollingStart", SENSOR_POLLING_START);

    byteToMethod.put(SENSOR_POLLING_STOP, "sensorPollingStop");
    methodToByte.put("sensorPollingStop", SENSOR_POLLING_STOP);

    byteToMethod.put(SERVO_ATTACH, "servoAttach");
    methodToByte.put("servoAttach", SERVO_ATTACH);

    byteToMethod.put(SERVO_DETACH, "servoDetach");
    methodToByte.put("servoDetach", SERVO_DETACH);

    byteToMethod.put(SERVO_EVENTS_ENABLED, "servoEventsEnabled");
    methodToByte.put("servoEventsEnabled", SERVO_EVENTS_ENABLED);

    byteToMethod.put(SERVO_SWEEP_START, "servoSweepStart");
    methodToByte.put("servoSweepStart", SERVO_SWEEP_START);

    byteToMethod.put(SERVO_SWEEP_STOP, "servoSweepStop");
    methodToByte.put("servoSweepStop", SERVO_SWEEP_STOP);

    byteToMethod.put(SERVO_WRITE, "servoWrite");
    methodToByte.put("servoWrite", SERVO_WRITE);

    byteToMethod.put(SERVO_WRITE_MICROSECONDS, "servoWriteMicroseconds");
    methodToByte.put("servoWriteMicroseconds", SERVO_WRITE_MICROSECONDS);

    byteToMethod.put(SET_DEBOUNCE, "setDebounce");
    methodToByte.put("setDebounce", SET_DEBOUNCE);

    byteToMethod.put(SET_DIGITAL_TRIGGER_ONLY, "setDigitalTriggerOnly");
    methodToByte.put("setDigitalTriggerOnly", SET_DIGITAL_TRIGGER_ONLY);

    byteToMethod.put(SET_LOAD_TIMING_ENABLED, "setLoadTimingEnabled");
    methodToByte.put("setLoadTimingEnabled", SET_LOAD_TIMING_ENABLED);

    byteToMethod.put(SET_PWMFREQUENCY, "setPWMFrequency");
    methodToByte.put("setPWMFrequency", SET_PWMFREQUENCY);

    byteToMethod.put(SET_SAMPLE_RATE, "setSampleRate");
    methodToByte.put("setSampleRate", SET_SAMPLE_RATE);

    byteToMethod.put(SET_SERIAL_RATE, "setSerialRate");
    methodToByte.put("setSerialRate", SET_SERIAL_RATE);

    byteToMethod.put(SET_SERVO_SPEED, "setServoSpeed");
    methodToByte.put("setServoSpeed", SET_SERVO_SPEED);

    byteToMethod.put(SET_TRIGGER, "setTrigger");
    methodToByte.put("setTrigger", SET_TRIGGER);

    byteToMethod.put(SOFT_RESET, "softReset");
    methodToByte.put("softReset", SOFT_RESET);

    // TODO: should I add this here?
    // byteToMethod.put(MESSAGE_ACK, "messageAck");
    // methodToByte.put("messageAck", MESSAGE_ACK);
  }
  ///// JAVA GENERATED DEFINITION END - DO NOT MODIFY //////

  static public String byteToMethod(int m) {
    if (byteToMethod.containsKey(m)) {
      return byteToMethod.get(m);
    }
    return null;
  }

  public static void main(String[] args) {

    try {
      LoggingFactory.getInstance().configure();
      LoggingFactory.getInstance().setLevel(Level.INFO);
      LoggingFactory.getInstance().addAppender("FILE");

      // begin ----

      log.info("===setUpBeforeClass===");
      // LoggingFactory.getInstance().setLevel(Level.INFO);
      Runtime.start("gui", "GUIService");
      Arduino arduino = (Arduino) Runtime.start("arduino", "Arduino");
      Serial serial = arduino.getSerial();
      serial.record("out");

      // rxtxLib
      arduino.connect("COM15");

      // arduino.connectTCP("localhost", 9191);

      arduino.pinMode(10, 1);
      arduino.digitalWrite(10, 1);
      arduino.analogReadPollingStart(0);
      // uart = serial.createVirtualUART();
      arduino.analogReadPollingStop(0);
      arduino.analogReadPollingStart(0);
      arduino.analogReadPollingStop(0);
      arduino.analogReadPollingStart(0);
      arduino.analogReadPollingStop(0);

      serial.stopRecording();
      // Test test = (org.myrobotlab.service.Test) Runtime.start("test",
      // "Test");

      // / end ---

      /*
       * ArduinoMsgCodec codec = new ArduinoMsgCodec();
       * 
       * FileOutputStream test = new FileOutputStream(new File("out.bin"));
       * 
       * for (int j = 0; j < 4; ++j) { for (int i = 0; i < 100; ++i) { int[]
       * data = codec.encode(String.format("publishPin/15/%d/%d\n", j, i)); for
       * (int z = 0; z < data.length; ++z){ test.write(data[z]); } } }
       * 
       * test.close();
       */

      /*
       * 
       * // digitalWrite/9/1 StringBuilder sb = new StringBuilder();
       * sb.append(codec.decode(170)); sb.append(codec.decode(3));
       * sb.append(codec.decode(7)); sb.append(codec.decode(9));
       * sb.append(codec.decode(1));
       * 
       * sb.append(codec.decode(170)); sb.append(codec.decode(3));
       * sb.append(codec.decode(7)); sb.append(codec.decode(11));
       * sb.append(codec.decode(0));
       * 
       * log.info(String.format("[%s]", sb.toString()));
       * 
       * codec.encode(sb.toString());
       * 
       * Arduino arduino = (Arduino) Runtime.start("arduino", "Arduino"); Serial
       * serial = arduino.getSerial(); serial.record();
       * serial.processRxByte(170); serial.processRxByte(3);
       * serial.processRxByte(7); serial.processRxByte(9);
       * serial.processRxByte(1);
       */

    } catch (Exception e) {
      Logging.logError(e);
    }

  }

  // /// JAVA GENERATED DEFINITION END - DO NOT MODIFY //////

  /**
   * MAGIC_NUMBER|NUM_BYTES|FUNCTION|DATA0|DATA1|....|DATA(N)
   * 
   * @throws CodecException
   */
  @Override
  public String decodeImpl(int newByte) {

    // log.info(String.format("byteCount %d", byteCount));
    ++byteCount;
    if (byteCount == 1 && newByte != MAGIC_NUMBER) {
      // reset - try again
      rest.setLength(0);
      byteCount = 0;
      decodeMsgSize = 0;

      error("bad magic number %d", newByte);
    }

    if (byteCount == 2) {
      // get the size of message
      // todo check msg < 64 (MAX_MSG_SIZE)
      decodeMsgSize = newByte;
    }

    // set method
    if (byteCount == 3) {
      rest.append(byteToMethod.get(newByte));
    }

    if (byteCount > 3) {
      // FIXME - for
      rest.append(String.format("/%d", newByte));
    }

    // if received header + msg
    if (byteCount == 2 + decodeMsgSize) {
      // msg done
      byteCount = 0;
      rest.append("\n");
      String ret = rest.toString();
      rest.setLength(0);
      byteCount = 0;
      decodeMsgSize = 0;
      return ret;
    }

    // not ready yet
    // no msg :P should be null ???
    return null;
  }

  @Override
  public String decode(int[] msgs) {
    if (msgs == null) {
      return new String("");
    }

    log.info(String.format("decoding input of %d bytes", msgs.length));

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < msgs.length; ++i) {
      sb.append(decode(msgs[i]));
    }

    return sb.toString();
  }

  // must maintain state - partial string
  @Override
  public int[] encode(String msgs) {

    // moved all member vars as local
    // otherwise state information would explode
    // cheap way of making threadsafe
    // this variables
    int pos = 0;
    int newLinePos = 0;
    int slashPos = 0;

    ArrayList<Integer> temp = new ArrayList<Integer>();
    ArrayList<Integer> data = new ArrayList<Integer>();

    // --

    if (msgs == null) {
      return new int[0];
    }

    // log.info(String.format("encoding input of %d characters",
    // msgs.length()));

    newLinePos = msgs.indexOf("\n", pos);
    slashPos = msgs.indexOf("/", pos);

    // while not done - string not completed...
    // make sure you leave in a good state if not a full String

    // FIXME test cases - newLinePos == -1 pos == -1 00 01 10 11

    // while either / or new line or eof (string) [eof leave vars in
    // unfinished state]
    while (slashPos != -1 || newLinePos != -1) {

      // ++currentLine;

      if (slashPos > 0 && newLinePos > 0 && slashPos < newLinePos) {
        // digitalWrite/9/1
        // pos^ slashpos ^ ^newLinePos
        if (temp.size() == 0) {
          String method = msgs.substring(pos, slashPos);
          pos = slashPos + 1;
          // found method
          if (methodToByte.containsKey(method)) {
            temp.add(methodToByte.get(method));
          } else {
            error("method [%s] at position %d is not defined for codec", method, pos);
            pos = 0;
            data.clear();
          }
        } else {
          // in data region
          String param = msgs.substring(pos, slashPos);
          temp.add(Integer.parseInt(param));
          pos = slashPos + 1;
        }
      } else if ((slashPos > 0 && newLinePos > 0 && newLinePos < slashPos) || (slashPos == -1 && newLinePos > 0)) {
        // end of message slash is beyond newline || newline exists and
        // slash does not
        String param = msgs.substring(pos, newLinePos);
        temp.add(Integer.parseInt(param));
        pos = newLinePos + 1;
        slashPos = pos;

        // unload temp buffer - start next message - if there is one
        data.add(170);// MAGIC NUMBER
        data.add(temp.size());// SIZE
        for (int i = 0; i < temp.size(); ++i) {
          // should be end of record
          data.add(temp.get(i));
        }
        // clear buffer - ready for next message
        temp.clear();
      }

      newLinePos = msgs.indexOf("\n", pos);
      slashPos = msgs.indexOf("/", pos);

    }

    int[] ret = new int[data.size()];
    // for (int i : data) {
    for (int i = 0; i < data.size(); ++i) {
      ret[i] = data.get(i);
    }

    // FIXME - more cases when pos is reset - or all vars reset?
    pos = 0;
    data.clear();
    return ret;
  }

  @Override
  public String getCodecExt() {
    return getKey().substring(0, 3);
  }

  @Override
  public String getKey() {
    return "arduino";
  }
  
  public static String functionToString(int function) {
    if (function == ArduinoMsgCodec.ANALOG_READ_POLLING_START)
      return "ANALOG_READ_POLLING_START";
    if (function == ArduinoMsgCodec.ANALOG_READ_POLLING_STOP)
      return "ANALOG_READ_POLLING_STOP";
    if (function == ArduinoMsgCodec.ANALOG_WRITE)
      return "ANALOG_WRITE";
    if (function == ArduinoMsgCodec.DIGITAL_READ_POLLING_START)
      return "DIGITAL_READ_POLLING_START";
    if (function == ArduinoMsgCodec.DIGITAL_READ_POLLING_STOP)
      return "DIGITAL_READ_POLLING_STOP";
    if (function == ArduinoMsgCodec.DIGITAL_WRITE)
      return "DIGITAL_WRITE";
    if (function == ArduinoMsgCodec.GET_VERSION)
      return "GET_VERSION";
    if (function == ArduinoMsgCodec.MOTOR_ATTACH)
      return "MOTOR_ATTACH";
    if (function == ArduinoMsgCodec.MOTOR_DETACH)
      return "MOTOR_DETACH";
    if (function == ArduinoMsgCodec.MOTOR_MOVE)
      return "MOTOR_MOVE";
    if (function == ArduinoMsgCodec.MOTOR_MOVE_TO)
      return "MOTOR_MOVE_TO";
    if (function == ArduinoMsgCodec.MOTOR_RESET)
      return "MOTOR_RESET";
    if (function == ArduinoMsgCodec.MOTOR_STOP)
      return "MOTOR_STOP";
    if (function == ArduinoMsgCodec.PIN_MODE)
      return "PIN_MODE";
    if (function == ArduinoMsgCodec.PULSE)
      return "PULSE";
    if (function == ArduinoMsgCodec.PULSE_STOP)
      return "PULSE_STOP";
    if (function == ArduinoMsgCodec.SENSOR_ATTACH)
      return "SENSOR_ATTACH";
    if (function == ArduinoMsgCodec.SENSOR_POLLING_START)
      return "SENSOR_POLLING_START";
    if (function == ArduinoMsgCodec.SENSOR_POLLING_STOP)
      return "SENSOR_POLLING_STOP";
    if (function == ArduinoMsgCodec.SERVO_ATTACH)
      return "SERVO_ATTACH";
    if (function == ArduinoMsgCodec.SERVO_DETACH)
      return "SERVO_DETACH";
    if (function == ArduinoMsgCodec.SERVO_EVENTS_ENABLED)
      return "SERVO_EVENTS_ENABLED";
    if (function == ArduinoMsgCodec.SERVO_SWEEP_START)
      return "SERVO_SWEEP_START";
    if (function == ArduinoMsgCodec.SERVO_SWEEP_STOP)
      return "SERVO_SWEEP_STOP";
    if (function == ArduinoMsgCodec.SERVO_WRITE)
      return "SERVO_WRITE";
    if (function == ArduinoMsgCodec.SERVO_WRITE_MICROSECONDS)
      return "SERVO_WRITE_MICROSECONDS";
    if (function == ArduinoMsgCodec.PUBLISH_MESSAGE_ACK)
      return "PUBLISH_MESSAGE_ACK";
    if (function == ArduinoMsgCodec.PUBLISH_SENSOR_DATA)
      return "PUBLISH_SENSOR_DATA";
    if (function == ArduinoMsgCodec.PUBLISH_PIN)
      return "PUBLISH_PIN";
    if (function == ArduinoMsgCodec.PUBLISH_PULSE)
      return "PUBLISH_PULSE";
    if (function == ArduinoMsgCodec.PUBLISH_PULSE_STOP)
      return "PUBLISH_PULSE_STOP";
    if (function == ArduinoMsgCodec.PUBLISH_TRIGGER)
      return "PUBLISH_TRIGGER";
    if (function == ArduinoMsgCodec.PUBLISH_VERSION)
      return "PUBLISH_VERSION";
    if (function == ArduinoMsgCodec.PUBLISH_LOAD_TIMING_EVENT)
      return "PUBLISH_LOAD_TIMING_EVENT";
    if (function == ArduinoMsgCodec.SET_LOAD_TIMING_ENABLED) 
      return "SET_LOAD_TIMING_ENABLED";
    if (function == ArduinoMsgCodec.PUBLISH_DEBUG) 
      return "PUBLISH_DEBUG";
    if (function == ArduinoMsgCodec.DEBUG_ENABLE) 
      return "DEBUG_ENABLE";
    if (function == ArduinoMsgCodec.DEBUG_DISABLE) 
      return "DEBUG_DISABLE";
    if (function == ArduinoMsgCodec.SET_PWMFREQUENCY)
      return "SET_PWMFREQUENCY";
    return "OTHER(" + Integer.toString(function) + ")";
    
  }

}
