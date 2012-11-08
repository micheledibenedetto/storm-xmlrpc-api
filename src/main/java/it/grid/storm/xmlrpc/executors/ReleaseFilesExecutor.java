package it.grid.storm.xmlrpc.executors;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.xmlrpc.ApiException;
import it.grid.storm.xmlrpc.decoders.DecodingException;
import it.grid.storm.xmlrpc.decoders.SurlArrayStatusDecoder;
import it.grid.storm.xmlrpc.encoders.FinalizeFileTransferEncoder;
import it.grid.storm.xmlrpc.outputdata.SurlArrayRequestOutputData;
import it.grid.storm.xmlrpc.remote.synchcall;
import java.util.List;
import java.util.Map;
import redstone.xmlrpc.XmlRpcFault;

public class ReleaseFilesExecutor
{
    public static SurlArrayRequestOutputData execute(synchcall storm, String userDN, List<String> userFQANS,
            List<String> surls, TRequestToken requestToken) throws ApiException
    {
        if (storm == null || userDN == null || userFQANS == null || userFQANS.isEmpty() || surls == null
                || surls.isEmpty() || requestToken == null)
        {
            throw new IllegalArgumentException("Unable to call rf command. Received null arguments: storm="
                    + (storm == null ? "null" : "not null") + " userDN=" + userDN + " userFQANS=" + userFQANS + " surls=" + surls
                    + " requestToken=" + requestToken);
        }
        Map<String, Object> parameters;
        try
        {
            parameters = FinalizeFileTransferEncoder.getInstance().encode(userDN, userFQANS, surls, requestToken);
        } catch(IllegalArgumentException e)
        {
            throw new ApiException("Unable to encode rf parameters. IllegalArgumentException: "
                    + e.getMessage());
        }
        return doIt(storm, parameters);
    }
    
    private static SurlArrayRequestOutputData doIt(synchcall storm, Map<String, Object> parameters) throws ApiException
    {
        Map<String, Object> output;
        try
        {
            output = storm.releaseFiles(parameters);
        } catch(XmlRpcFault e)
        {
            throw new ApiException("Unable to perform rf call. XmlRpcFault: " + e.getMessage());
        }
        try
        {
            return SurlArrayStatusDecoder.getInstance().decode(output);
        } catch(IllegalArgumentException e)
        {
            throw new ApiException("Unable to decode rf call output. IllegalArgumentException: "
                    + e.getMessage());
        } catch(DecodingException e)
        {
            throw new ApiException("Unable to decode rf call output. DecodingException: " + e.getMessage());
        }
    }


}