package lvc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lvc.repos.LogisticRepository;
import lvc.repos.PairRepository;
import lvc.repos.VesselRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CoreController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PairRepository pairRepository;

    @Autowired
    private LogisticRepository logisticRegistory;

    @Autowired
    private VesselRepository vesselRepository;

}
