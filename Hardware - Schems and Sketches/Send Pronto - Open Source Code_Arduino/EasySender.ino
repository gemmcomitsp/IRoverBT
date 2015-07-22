/*
 * IRwebremote: send remote signals via the web
 * An IR LED must be connected to Arduino PWM pin 3.
 * Codes are received over the serial port and sent on pin 3.
 * Syntax:
 * N12345678  - send 32-bit NEC code
 * S00000123  - send 12-bit Sony code
 * r00000123  - send 12-bit RC5 code
 * R00012345  - send 20-bit RC6 code
 * Copyright 2009 Ken Shirriff
 * http://arcfn.com
 */

#include <IRremote.h>

IRsend irsend;
// Always the Syntax code have 9 characters , first the letter follow code. its code is less of 8 character , complete to 8 character with 0//
// example : sony = a90 change to S00000a90
#define PanasonicAddress 0x4004
#define NEC 'N'          //N12345678
#define SONY 'S'         //S00000123
#define RC5 'r'          //r00000123
#define RC6 'R'          //R00012345
#define PANASONIC 'P'    //P00012345
#define SAMSUNG 'U'      //U00012345
#define JVC 'J'          //J00012345




void setup()
{
  Serial.begin(9600);
}

unsigned long rctoggle = 0;

void processSerialCode() {
  if (Serial.available() < 9) return;
  char type = Serial.read();
  if (type != NEC && type != SONY && type != RC5 && type != RC6 && type != PANASONIC && type != JVC && type != SAMSUNG) {
    Serial.print("Unexpected type: ");
    Serial.println(type);
    return;
  }
  // Read 8 hex characters
  unsigned long code = 0;
  for (int i = 0; i < 8; i++) {
    char ch = Serial.read();
    if (ch >= '0' && ch <= '9') {
      code = code * 16 + ch - '0';
    } 
    else if (ch >= 'a' && ch <= 'f') {
      code = code * 16 + ch - 'a' + 10;
    } 
    else if (ch >= 'A' & ch <= 'F') {
      code = code * 16 + ch - 'A' + 10;
    } 
    else {
      Serial.print("Unexpected hex char: ");
      Serial.println(ch);
      return;
    }
  }
  if (type == NEC) {
    irsend.sendNEC(code, 32);
  } 
  else if (type == SONY) {
    // Send Sony code 3 times
    irsend.sendSony(code, 12);
    delay(50);
    irsend.sendSony(code, 12);
    delay(50);
    irsend.sendSony(code, 12);
  } 
  else if (type == RC5) {
    // Filp the RC5 toggle bit
    code = code ^ (rctoggle << 11);
    rctoggle = 1 - rctoggle;
    irsend.sendRC5(code, 12);
  } 
  else if (type == RC6) {
    // Flip the RC6 toggle bit
    code = code ^ (rctoggle << 16);
    rctoggle = 1 - rctoggle;
    irsend.sendRC6(code, 20);
  }
  else if (type == PANASONIC) {
    irsend.sendPanasonic(PanasonicAddress,code);
  }
  else if (type == JVC) {
    irsend.sendJVC(code, 16,0); 
    delayMicroseconds(50); 
    irsend.sendJVC(code, 16,1); 
    delayMicroseconds(50);
  }
  else if (type == SAMSUNG) {
    irsend.sendSAMSUNG(code, 32);
  }
  Serial.print("Sent code: ");
  Serial.println(code, HEX);
}

void loop() {
  processSerialCode();
}

