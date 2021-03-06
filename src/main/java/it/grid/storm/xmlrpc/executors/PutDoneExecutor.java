package it.grid.storm.xmlrpc.executors;


/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2012
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.xmlrpc.ApiException;
import it.grid.storm.xmlrpc.decoders.DecodingException;
import it.grid.storm.xmlrpc.decoders.SurlArrayStatusDecoder;
import it.grid.storm.xmlrpc.encoders.ManageFileTransferEncoder;
import it.grid.storm.xmlrpc.outputdata.SurlArrayRequestOutputData;
import it.grid.storm.xmlrpc.remote.synchcall;
import java.util.List;
import java.util.Map;
import redstone.xmlrpc.XmlRpcFault;

/**
 * @author Michele Dibenedetto
 */
public class PutDoneExecutor
{

    public static SurlArrayRequestOutputData execute(synchcall storm, String userDN, List<String> userFQANS,
            List<String> surls, TRequestToken requestToken) throws ApiException
    {
        if (storm == null || userDN == null || userFQANS == null || userFQANS.isEmpty() || surls == null
                || surls.isEmpty() || requestToken == null)
        {
            throw new IllegalArgumentException("Unable to call pd command. Received null arguments: storm="
                    + (storm == null ? "null" : "not null") + " userDN=" + userDN + " userFQANS=" + userFQANS + " surls=" + surls
                    + " requestToken=" + requestToken);
        }
        Map<String, Object> parameters;
        try
        {
            parameters = ManageFileTransferEncoder.getInstance().encodeWithSurls(userDN, userFQANS, surls, requestToken);
        } catch(IllegalArgumentException e)
        {
            throw new ApiException("Unable to encode pd parameters. IllegalArgumentException: "
                    + e.getMessage());
        }
        return doIt(storm, parameters);
    }
    

    public static SurlArrayRequestOutputData execute(synchcall storm, String userDN, List<String> surls,
            TRequestToken requestToken) throws ApiException
    {
        if (storm == null || userDN == null || surls == null
                || surls.isEmpty() || requestToken == null)
        {
            throw new IllegalArgumentException("Unable to call pd command. Received null arguments: storm="
                    + (storm == null ? "null" : "not null") + " userDN=" + userDN + " surls=" + surls
                    + " requestToken=" + requestToken);
        }
        Map<String, Object> parameters;
        try
        {
            parameters = ManageFileTransferEncoder.getInstance().encodeWithSurls(userDN, surls, requestToken);
        } catch(IllegalArgumentException e)
        {
            throw new ApiException("Unable to encode pd parameters. IllegalArgumentException: "
                    + e.getMessage());
        }
        return doIt(storm, parameters);
    }

    public static SurlArrayRequestOutputData execute(synchcall storm, List<String> surls,
            TRequestToken requestToken) throws ApiException
    {
        if (storm == null || surls == null
                || surls.isEmpty() || requestToken == null)
        {
            throw new IllegalArgumentException("Unable to call pd command. Received null arguments: storm="
                    + (storm == null ? "null" : "not null") + " surls=" + surls
                    + " requestToken=" + requestToken);
        }
        Map<String, Object> parameters;
        try
        {
            parameters = ManageFileTransferEncoder.getInstance().encodeWithSurls(surls, requestToken);
        } catch(IllegalArgumentException e)
        {
            throw new ApiException("Unable to encode pd parameters. IllegalArgumentException: "
                    + e.getMessage());
        }
        return doIt(storm, parameters);
    }
    
    private static SurlArrayRequestOutputData doIt(synchcall storm, Map<String, Object> parameters) throws ApiException
    {
        Map<String, Object> output;
        try
        {
            output = storm.putDone(parameters);
        } catch(XmlRpcFault e)
        {
            throw new ApiException("Unable to perform pd call. XmlRpcFault: " + e.getMessage());
        }
        try
        {
            return SurlArrayStatusDecoder.getInstance().decode(output);
        } catch(IllegalArgumentException e)
        {
            throw new ApiException("Unable to decode pd call output. IllegalArgumentException: "
                    + e.getMessage());
        } catch(DecodingException e)
        {
            throw new ApiException("Unable to decode pd call output. DecodingException: " + e.getMessage());
        }
    }


}
