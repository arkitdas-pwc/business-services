/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.dsc.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.dsc.config.ApplicationProperties;
import org.egov.dsc.emas.ws.ds.DSAuthenticateWS;
import org.egov.dsc.emas.ws.ds.DSAuthenticateWSProxy;
import org.egov.dsc.model.CertificateRequest;
import org.egov.dsc.model.CertificateResponse;
import org.egov.dsc.model.CertificateResponsePojo;
import org.egov.dsc.model.DataSignRequest;
import org.egov.dsc.model.DataSignResponse;
import org.egov.dsc.model.DetailRequestPojo;
import org.egov.dsc.model.FileReq;
import org.egov.dsc.model.StorageResponse;
import org.egov.dsc.model.TokenInputResponse;
import org.egov.dsc.model.TokenRequest;
import org.egov.dsc.model.TokenResponse;
import org.egov.dsc.web.contract.factory.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import emh.Enum.ContentType;
import emh.Enum.Coordinates;
import emh.Enum.PageTobeSigned;
import emh.Enum.Token_Status;
import emh.Enum.Token_Type;
import emh.Model.RequestData.ListCertificateRequest;
import emh.Model.RequestData.ListTokenRequest;
import emh.Model.RequestData.PKCSBulkPdfHashSignRequest;
import emh.Model.RequestData.PKCSSignRequest;
import emh.Model.RequestData.Request;
import emh.Model.ResponseData.BulkSignOutput;
import emh.Model.ResponseData.PKCSCertificate;
import emh.Model.ResponseData.ProviderToken;
import emh.Model.ResponseData.ResponseDataListPKCSCertificate;
import emh.Model.ResponseData.ResponseDataListProviderToken;
import emh.Model.ResponseData.ResponseDataPKCSBulkSign;
import emh.Model.ResponseData.ResponseDataPKCSSign;
import emh.emBridgeLib.EMBLException;
import emh.emBridgeLib.emBridge;
import emh.emBridgeLib.emBridgeSignerInput;

@RestController
@RequestMapping("/dsc")
public class DscController {

	@Autowired
	private ApplicationProperties applicationProperties;
    
	@Autowired
    private RestTemplate restTemplate;

	@GetMapping(value="/_getCheck")
	public String test()
	{
		return applicationProperties.getLicPath();
	}
	
    @RequestMapping(value = "/_getTokenInput", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TokenInputResponse> token(@RequestBody  TokenRequest tokenRequest)  {
    	emBridge bridge = null;
		try {
			bridge = new emBridge(applicationProperties.getLicPath(),applicationProperties.getTokenLogPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	ListTokenRequest listTokenRequest = new ListTokenRequest();
    	listTokenRequest.setTokenStatus(Token_Status.CONNECTED);
    	listTokenRequest.setTokenType(Token_Type.HARDWARE); 
    	Request data = null;
		try {
			data = bridge.encListToken(listTokenRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}

    	System.out.println("Encrypted Data :"+data.getEncryptedData());
    	System.out.println("Encrypted Key ID :"+data.getEncryptionKeyID());	

    	DetailRequestPojo input=new DetailRequestPojo();
    	input.setEncryptedRequest(data.getEncryptedData());
    	input.setEncryptionKeyId(data.getEncryptionKeyID());
    	
        return getSuccessTokenInputResponse(input, tokenRequest.getRequestInfo());

    }
    
    @RequestMapping(value = "/_getTokens", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TokenResponse> tokens(@RequestBody  TokenRequest tokenRequest)  {
    	List<String> tokens=new ArrayList<String>();
    	emBridge bridge = null;
		try {
			bridge = new emBridge(applicationProperties.getLicPath(),applicationProperties.getTokenLogPath());
		} catch (IOException e) {
			e.printStackTrace();
		}

    	ResponseDataListProviderToken responseDataListProviderToken = bridge.decListToken(tokenRequest.getResponseData()); 
    	List<ProviderToken> tokens1 = responseDataListProviderToken.getTokens();
    	for(ProviderToken token : tokens1) {	
    		System.out.println(token.getKeyStoreDisplayName());
    		tokens.add(token.getKeyStoreDisplayName());
    	}
    	
        return getSuccessTokenResponse(tokens, tokenRequest.getRequestInfo());

    }
    
    @RequestMapping(value = "/_getInputCertificate", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TokenInputResponse> cert(@RequestBody CertificateRequest certificateRequest)  {
    	emBridge bridge = null;
		try {
			bridge = new emBridge(applicationProperties.getLicPath(),applicationProperties.getCertLogPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		ListCertificateRequest listCertRequest = new ListCertificateRequest();
		listCertRequest.setKeyStoreDisplayName(certificateRequest.getTokenDisplayName());//Microsoft Windows Store//ePass V 
    	Request data = null;
		try {
			data = bridge.encListCertificate(listCertRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}

    	System.out.println("Encrypted Data :"+data.getEncryptedData());
    	System.out.println("Encrypted Key ID :"+data.getEncryptionKeyID());	

    	DetailRequestPojo input=new DetailRequestPojo();
    	input.setEncryptedRequest(data.getEncryptedData());
    	input.setEncryptionKeyId(data.getEncryptionKeyID());
    	
        return getSuccessTokenInputResponse(input, certificateRequest.getRequestInfo());
    }
    
    @RequestMapping(value = "/_getCertificate", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<CertificateResponse> certa(@RequestBody  CertificateRequest certificateRequest)  {
    	emBridge bridge = null;
		try {
			bridge = new emBridge(applicationProperties.getLicPath(),applicationProperties.getCertLogPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	 ResponseDataListPKCSCertificate responseDataListPKCSCertificate = null;
		try {
			responseDataListPKCSCertificate = bridge.decListCertificate(certificateRequest.getResponseData(),null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	 System.out.println(responseDataListPKCSCertificate.getCertificates().size());
    	 List<PKCSCertificate> certificates = responseDataListPKCSCertificate.getCertificates();
    	 List<CertificateResponsePojo> certificatesList=new ArrayList<CertificateResponsePojo>();
    	 CertificateResponsePojo certificate=null;
    	 for(PKCSCertificate cert : certificates) {
    				System.out.println("value of keyId :" + cert.getKeyId());
    				System.out.println("value of common name :" + cert.getCommonName());
    				System.out.println("value of expiry :"+cert.getValidTill());
    				System.out.println("value of Certificate data :"+cert.getCertificateData());
    				certificate=new CertificateResponsePojo();
    				certificate.setKeyId(cert.getKeyId());
    				certificate.setCommonName(cert.getCommonName());
    				certificate.setCertificateDate(cert.getCertificateData());
    				certificatesList.add(certificate);
    	 }
    	
        return getSuccessCertResponse(certificatesList, certificateRequest.getRequestInfo());

    }
    
    
    @RequestMapping(value = "/_dataSignInput", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TokenInputResponse> dataSignInput(@RequestBody DataSignRequest dataSignRequest)  {
    	emBridge bridge = null;
		try {
			bridge = new emBridge(applicationProperties.getLicPath(),applicationProperties.getDataSignLogPath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
		PKCSSignRequest pKCSSignRequest = new PKCSSignRequest();
		pKCSSignRequest.setKeyStoreDisplayName(dataSignRequest.getTokenDisplayName());//token
		pKCSSignRequest.setKeyStorePassphrase(dataSignRequest.getKeyStorePassPhrase());
		pKCSSignRequest.setKeyId(dataSignRequest.getKeyId());
		
		pKCSSignRequest.setDataType(ContentType.TextPKCS7ATTACHED); //detached data signature
		
		// emas random number execute
		String randomNumber=populateRandom(dataSignRequest.getRequestInfo().getUserInfo().getId(),dataSignRequest.getChannelId());
		pKCSSignRequest.setDataToSign(randomNumber);  
		pKCSSignRequest.setTimeStamp("");
		Request data = null;
		try {
			data = bridge.encPKCSSign(pKCSSignRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    System.out.println("Encrypted Data :"+data.getEncryptedData());
		System.out.println("Encrypted Key ID :"+data.getEncryptionKeyID());	
		DetailRequestPojo input=new DetailRequestPojo();
		input.setEncryptedRequest(data.getEncryptedData());
		input.setEncryptionKeyId(data.getEncryptionKeyID());
		
	
    	
        return getSuccessTokenInputResponse(input, dataSignRequest.getRequestInfo());
        
    }
    
    @RequestMapping(value = "/_dataSign", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<DataSignResponse> dataSign(@RequestBody  DataSignRequest dataSignRequest)  {
    	emBridge bridge = null;
		try {
			bridge = new emBridge(applicationProperties.getLicPath(),applicationProperties.getDataSignLogPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		ResponseDataPKCSSign responseDataPKCSSign = null;
		try {
			responseDataPKCSSign = bridge.decPKCSSign(dataSignRequest.getResponseData());
		} catch (EMBLException e) {
			e.printStackTrace();
		}
		System.out.println("\n\nvalue of Data Sign is111:\n");
		System.out.println("Signed Text :"+responseDataPKCSSign.getSignedText());
		//register
		String repsonse=populateRegisterSoapCall(dataSignRequest.getRequestInfo().getUserInfo().getId(),dataSignRequest.getChannelId(),responseDataPKCSSign.getSignedText());
    	
        return getSuccessDataSignResponse(repsonse, null,dataSignRequest.getRequestInfo());
        
    }

	private String populateRegisterSoapCall(Long id,String channel,String signedText) {
		

		  String result="";
		  DSAuthenticateWS authenticateWS = new DSAuthenticateWSProxy(applicationProperties.getEmasWsUrl());
		  
		  try {
			result = authenticateWS.register(id+"`"+channel,signedText,null,null,false);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		  return result;
		  
	}

	
	  private String populateRandom(Long id,String channel) {
		  String result="";
		  DSAuthenticateWS authenticateWS =null;
		  try {
		   authenticateWS = new DSAuthenticateWSProxy(applicationProperties.getEmasWsUrl());
			result = authenticateWS.generateRandomNumber (id+"~"+channel);
			result=result.split("~")[1];
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		  return result;
		  }
	 
	

	private ResponseEntity<TokenResponse> getSuccessTokenResponse(List<String> tokens, RequestInfo requestInfo) {
        final ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);
        responseInfo.setStatus(HttpStatus.OK.toString());

        TokenResponse tokenResponse = new TokenResponse(responseInfo, tokens);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }
	
	private ResponseEntity<TokenInputResponse> getSuccessTokenInputResponse(DetailRequestPojo input, RequestInfo requestInfo) {
        final ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);
        responseInfo.setStatus(HttpStatus.OK.toString());

        TokenInputResponse tokenResponse = new TokenInputResponse(responseInfo, input);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }
	
	
	private ResponseEntity<CertificateResponse> getSuccessCertResponse(List<CertificateResponsePojo> certs, RequestInfo requestInfo) {
        final ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);
        responseInfo.setStatus(HttpStatus.OK.toString());

        CertificateResponse certificateResponse = new CertificateResponse(responseInfo, certs);
        return new ResponseEntity<>(certificateResponse, HttpStatus.OK);
    }
	
	private ResponseEntity<DataSignResponse> getSuccessDataSignResponse(String responseString,String fileStoreId,  RequestInfo requestInfo) {
        final ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);
        responseInfo.setStatus(HttpStatus.OK.toString());

        DataSignResponse datasign = new DataSignResponse(responseInfo, responseString,fileStoreId);
        return new ResponseEntity<>(datasign, HttpStatus.OK);
    }
	
	@RequestMapping(value = "/_pdfSignInput", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TokenInputResponse> dpdfSignInput(@RequestBody DataSignRequest dataSignRequest)  {
    	emBridge bridge = null;
		try {
			bridge = new emBridge(applicationProperties.getLicPath(),applicationProperties.getDataSignLogPath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		List<emBridgeSignerInput> inputs = new ArrayList<>();
		emBridgeSignerInput input = new emBridgeSignerInput(dataSignRequest.getFileBytes(),dataSignRequest.getFileName(),applicationProperties.getPdfProperty1(), applicationProperties.getPdfProperty2(),applicationProperties.getPdfProprty3(),true, PageTobeSigned.Last, Coordinates.BottomRight, applicationProperties.getPdfProprty4(), false);
		inputs.add(input);
		PKCSBulkPdfHashSignRequest pKCSBulkPdfHashSignRequest = new PKCSBulkPdfHashSignRequest();
		pKCSBulkPdfHashSignRequest.setBulkInput(inputs);
		pKCSBulkPdfHashSignRequest.setTempFolder(applicationProperties.getServerTempPath());
		pKCSBulkPdfHashSignRequest.setKeyStoreDisplayName(dataSignRequest.getTokenDisplayName());
		pKCSBulkPdfHashSignRequest.setKeyStorePassphrase(dataSignRequest.getKeyStorePassPhrase());
		pKCSBulkPdfHashSignRequest.setKeyId(dataSignRequest.getKeyId());
		Request bulkPKCSSignRequest = null;
		try {
			bulkPKCSSignRequest = bridge.encPKCSBulkSign(pKCSBulkPdfHashSignRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Encrypted Data :" + bulkPKCSSignRequest.getEncryptedData());
		System.out.println("Encrypted Key ID :" + bulkPKCSSignRequest.getEncryptionKeyID());
    	
		DetailRequestPojo pojo=new DetailRequestPojo();
    	pojo.setEncryptedRequest(bulkPKCSSignRequest.getEncryptedData());
    	pojo.setEncryptionKeyId(bulkPKCSSignRequest.getEncryptionKeyID());
    	pojo.setFileName(dataSignRequest.getFileName());
        return getSuccessTokenInputResponse(pojo, dataSignRequest.getRequestInfo());
        
    }
    
    @RequestMapping(value = "/_pdfSign", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<DataSignResponse> pdfSign(@RequestBody  DataSignRequest dataSignRequest)  {
    	emBridge bridge = null;
		try {
			bridge = new emBridge(applicationProperties.getLicPath(),applicationProperties.getDataSignLogPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		ResponseDataPKCSBulkSign apiResponse = bridge.decPKCSBulkSign(dataSignRequest.getResponseData(), applicationProperties.getServerTempPath());
		String fileId = null;
		try {
			fileId = populateSignedPdfFileStoreId(apiResponse,applicationProperties.getServerTempPath(),dataSignRequest.getFileName(),dataSignRequest.getRequestInfo().getUserInfo().getId(),dataSignRequest.getTennantId(),dataSignRequest.getModuleName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String result="";
		if(fileId != null && !fileId.isEmpty() && !fileId.equalsIgnoreCase("0"))
		{
			result="Authentication Successfull";
		}
		else
		{
			fileId=null;
			result="Authentication Failure";
		}
    	
        return getSuccessDataSignResponse(result,fileId,dataSignRequest.getRequestInfo());
        
    }

	private String populateSignedPdfFileStoreId(ResponseDataPKCSBulkSign apiResponse, String serverTempPath,
			String fileName, Long userId,String tennantId, String moduleName) throws IOException {
		String fileStoreId="0";
		File file =null;
		for (BulkSignOutput doc : apiResponse.getBulkSignItems()) {
			if(!checkPdfAuthentication(String.valueOf(userId),doc.getSignedData()))
			{
				break;
			}
			
			byte[] signedDocBytes = Base64.decodeBase64(doc.getSignedData());
			String finalFilePath = serverTempPath+"\\"+fileName+"_signed.pdf";
			 file = new File(finalFilePath);
			OutputStream os = new FileOutputStream(file);
			os.write(signedDocBytes);
			os.close();
		}
		fileStoreId=store(new FileInputStream(file),file.getName(),"application/pdf",moduleName,true,tennantId);
		file.delete();
		return fileStoreId;
	}

	public String store(InputStream fileStream, String fileName, String mimeType, String moduleName,
            boolean closeStream,String tennantId) {
		String fileStoreId=null;
        try {          
            byte[] fileSize = fileName.getBytes();
            DiskFileItem fileItem = new DiskFileItem("file",mimeType, false, fileName, fileSize.length, null);
            OutputStream os = fileItem.getOutputStream();
            int ret = fileStream.read();
            while ( ret != -1 )
            {
                os.write(ret);
                ret = fileStream.read();
            }
            os.flush();
            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
            fileStoreId = storeFiles(Arrays.asList(multipartFile),
                    fileName,
                    mimeType, moduleName,false,tennantId);
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return fileStoreId;
    }
	
	private String storeFiles(List<MultipartFile> files, String fileName, String mimeType, String moduleName,
            boolean deleteFile,String tennantId) {
			String fileStoreId=null;
        try {
            StorageResponse storageRes = getFileStorageService(files, moduleName,tennantId);

            List<FileReq> filesList = storageRes.getFiles();
            for (FileReq filesId : filesList) {
            	fileStoreId=filesId.getFileStoreId();
            }
            
            
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return fileStoreId;
    }
	
	public StorageResponse getFileStorageService(final List<MultipartFile> files, String modulename,String tennantId)
	        throws IOException {
	    HttpHeaders headers = new HttpHeaders();
	    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
	    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

	    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
	    map.add("module", modulename);
	    map.add("tenantId", tennantId);

	    ByteArrayResource contentsAsResource = null;
	    for (MultipartFile file : files) {
	        contentsAsResource = new ByteArrayResource(file.getBytes()) {
	            @Override
	            public String getFilename() {
	                return file.getOriginalFilename();
	            }
	        };
	    };
	    map.add("file", contentsAsResource);
	    //map.add("file", contentsAsResource);
	   
	    StringBuilder uri = new StringBuilder(applicationProperties.getEgovFileStoreSerHost())
	            .append(applicationProperties.getEgovFileStoreUploadFile());

	    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);

	    return restTemplate.postForObject(uri.toString(), request, StorageResponse.class);
	}

	private boolean checkPdfAuthentication(String userId, String signedData) {
		boolean result=false;
		DSAuthenticateWS authenticateWS = new DSAuthenticateWSProxy(applicationProperties.getEmasWsUrl());
		String authenticatePDF =null;
		try {
			authenticatePDF = authenticateWS.authenticatePDF(userId, signedData , null, "authenticate");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if(authenticatePDF != null && !authenticatePDF.isEmpty() && authenticatePDF.contains("Success"))
		{
			result=true;
		}
		return result;
	}

	

}
