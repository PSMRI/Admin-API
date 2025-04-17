/*
* AMRIT – Accessible Medical Records via Integrated Technology 
* Integrated EHR (Electronic Health Records) Solution 
*
* Copyright (C) "Piramal Swasthya Management and Research Institute" 
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/
package com.iemr.admin.controller.createorder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.admin.data.createOrder.CreateOrderData;
import com.iemr.admin.utils.mapper.InputMapper;
import com.iemr.admin.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;


@RestController
public class CareStreamCreateOrderController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	private static final char END_OF_BLOCK = '\u001c';
	private static final char START_OF_BLOCK = '\u000b';
	private static final char CARRIAGE_RETURN = 13;

	
	@Operation(summary = "Create order")
	@RequestMapping(value = "/createOrder", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String createOrder(@RequestBody String createOrder) throws UnknownHostException, IOException {
		OutputResponse response = new OutputResponse();

		try {

			CreateOrderData benificiaryDetails = InputMapper.gson().fromJson(createOrder, CreateOrderData.class);

			String firstName = benificiaryDetails.getFirstName();
			String middleName = benificiaryDetails.getMiddleName();
			String lastName = benificiaryDetails.getLastName();
			String patientID = benificiaryDetails.getPatientID();
			String dob = benificiaryDetails.getDob();
			String gender = benificiaryDetails.getGender();
			String acc = benificiaryDetails.getAcc();

			Socket socket = new Socket("192.168.1.199", 1235);
			System.out.println("Connected to Server");

			StringBuffer testHL7MassageToTransmit = new StringBuffer();
			testHL7MassageToTransmit.append(START_OF_BLOCK)
					.append("MSH|^~"
							+ "\"&|ImageSuite||||20120730000000.996||ORM^O01|MSG02010101_1|P|2.5|||AL||USA|ASCII||||")
					.append(CARRIAGE_RETURN)
					.append("PID|||" + patientID + "||" + firstName + "^" + lastName + "^||" + dob + "|" + gender
							+ "|||||||||||||||||||||||||||Species Code|Breed Code")
					.append(CARRIAGE_RETURN)
					.append("ORC|NW||" + acc + "||||20110621|||||Referring Physician Felix||||||||||||||||||")
					.append(CARRIAGE_RETURN)
					.append("OBR|0001|||CR_CHEST_PA|||20110622000000.996|||||||||Referring Physician Felix|||||||||||20110624|||||||||20110624||||||||CR_CHEST_PA|||||")
					.append(END_OF_BLOCK).append(CARRIAGE_RETURN);

			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			out.write(testHL7MassageToTransmit.toString().getBytes());

			byte[] byteBuffer = new byte[50];
			in.read(byteBuffer);

			response.setResponse("Receiver from server: " + new String(byteBuffer));

			String ip = "localhost";
			int port = 8082;

			String testMassage = "This is the test Massage that the client will transmit";
			byte[] byteBuffer1 = testMassage.getBytes();
			Socket socket1 = new Socket(ip, port);
			System.out.println("connected to the Server");
			InputStream in1 = socket.getInputStream();
			OutputStream out1 = socket.getOutputStream();
			out1.write(byteBuffer);
			in1.read(byteBuffer);
			System.out.println("Massage received form Server11" + new String(byteBuffer));

			socket.close();

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();
	}

	
	@Operation(summary = "Update order")
	@RequestMapping(value = "/UpdateOrder", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String UpdateOrder(@RequestBody String UpdateOrder) throws UnknownHostException, IOException {
		OutputResponse response = new OutputResponse();

		try {

			CreateOrderData benificiaryDetails = InputMapper.gson().fromJson(UpdateOrder, CreateOrderData.class);

			String firstName = benificiaryDetails.getFirstName();
			String middleName = benificiaryDetails.getMiddleName();
			String lastName = benificiaryDetails.getLastName();
			String patientID = benificiaryDetails.getPatientID();
			String dob = benificiaryDetails.getDob();
			String gender = benificiaryDetails.getGender();
			String acc = benificiaryDetails.getAcc();

			Socket socket = new Socket("192.168.1.101", 1235);
			System.out.println("Connected to Server");

			StringBuffer testHL7MassageToTransmit = new StringBuffer();
			testHL7MassageToTransmit.append(START_OF_BLOCK)
					.append("MSH|^~"
							+ "\"&|ImageSuite||||20120730000000.996||ORM^O01|MSG02010101_1|P|2.5|||AL||USA|ASCII||||")
					.append(CARRIAGE_RETURN)
					.append("PID|||" + patientID + "||" + firstName + "^" + lastName + "^||" + dob + "|" + gender
							+ "|||||||||||||||||||||||||||Species Code|Breed Code")
					.append(CARRIAGE_RETURN)
					.append("ORC|XO||" + acc + "||||20110621|||||Referring Physician Felix||||||||||||||||||")
					.append(CARRIAGE_RETURN)
					.append("OBR|0001|||CR_CHEST_PA|||20110622000000.996|||||||||Referring Physician Felix|||||||||||20110624|||||||||20110624||||||||CR_CHEST_PA|||||")
					.append(END_OF_BLOCK).append(CARRIAGE_RETURN);

			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			out.write(testHL7MassageToTransmit.toString().getBytes());

			byte[] byteBuffer = new byte[200];
			in.read(byteBuffer);

			response.setResponse("Receiver from server: " + new String(byteBuffer));

			socket.close();

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();
	}

	
	@Operation(summary = "Delete order")
	@RequestMapping(value = "/deleteOrder", headers = "Authorization", method = { RequestMethod.POST }, produces = {
			"application/json" })
	public String deleteOrder(@RequestBody String deleteOrder) throws UnknownHostException, IOException {
		OutputResponse response = new OutputResponse();

		try {

			CreateOrderData benificiaryDetails = InputMapper.gson().fromJson(deleteOrder, CreateOrderData.class);

			String firstName = benificiaryDetails.getFirstName();
			String middleName = benificiaryDetails.getMiddleName();
			String lastName = benificiaryDetails.getLastName();
			String patientID = benificiaryDetails.getPatientID();
			String dob = benificiaryDetails.getDob();
			String gender = benificiaryDetails.getGender();
			String acc = benificiaryDetails.getAcc();

			Socket socket = new Socket("192.168.1.101", 1235);
			System.out.println("Connected to Server");

			StringBuffer testHL7MassageToTransmit = new StringBuffer();
			testHL7MassageToTransmit.append(START_OF_BLOCK)
					.append("MSH|^~"
							+ "\"&|ImageSuite||||20120730000000.996||ORM^O01|MSG02010101_1|P|2.5|||AL||USA|ASCII||||")
					.append(CARRIAGE_RETURN)
					.append("PID|||" + patientID + "||" + firstName + "^" + lastName + "^||" + dob + "|" + gender
							+ "|||||||||||||||||||||||||||Species Code|Breed Code")
					.append(CARRIAGE_RETURN)
					.append("ORC|CA||" + acc + "||||20110621|||||Referring Physician Felix||||||||||||||||||")
					.append(CARRIAGE_RETURN)
					.append("OBR|0001|||CR_CHEST_PA|||20110622000000.996|||||||||Referring Physician Felix|||||||||||20110624|||||||||20110624||||||||CR_CHEST_PA|||||")
					.append(END_OF_BLOCK).append(CARRIAGE_RETURN);

			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			out.write(testHL7MassageToTransmit.toString().getBytes());

			byte[] byteBuffer = new byte[200];
			in.read(byteBuffer);

			response.setResponse("Receiver from server: " + new String(byteBuffer));

			socket.close();

		} catch (Exception e) {

			logger.error("Unexpected error:", e);
			response.setError(e);

		}

		return response.toString();
	}

}
