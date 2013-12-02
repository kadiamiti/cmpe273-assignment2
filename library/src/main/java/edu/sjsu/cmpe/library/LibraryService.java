package edu.sjsu.cmpe.library;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

import edu.sjsu.cmpe.library.api.resources.BookResource;
import edu.sjsu.cmpe.library.api.resources.RootResource;
import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.domain.Book;
import edu.sjsu.cmpe.library.repository.BookRepository;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.ui.resources.HomeResource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LibraryService extends Service<LibraryServiceConfiguration> {

	BookRepositoryInterface bookRepository = new BookRepository();
    private final Logger log = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) throws Exception {
	new LibraryService().run(args);
    }

    @Override
    public void initialize(Bootstrap<LibraryServiceConfiguration> bootstrap) {
	bootstrap.setName("library-service");
	bootstrap.addBundle(new ViewBundle());
    }

    @Override
    public void run(LibraryServiceConfiguration configuration,
	    Environment environment) throws Exception {
	String queueName = configuration.getStompQueueName();
	String topicName = configuration.getStompTopicName();
	String libraryName = configuration.getLibraryName();
	
	log.debug("Queue name is {}. Topic name is {}", queueName,
		topicName);
	
//Will pick up the variables from the config file of individual library	
	
	String apolloUser = configuration.getApolloUser();
	String apolloPassword = configuration.getApolloPassword();
	String apolloHost = configuration.getApolloHost();
	int apolloPort = configuration.getApolloPort();
	StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
	factory.setBrokerURI("tcp://" + apolloHost + ":" + apolloPort);
	
	Connection producerConnection = factory.createConnection(apolloUser, apolloPassword);
	producerConnection.start();
	Session producerSession = producerConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	Destination proddest = new StompJmsDestination(queueName);
	MessageProducer producer = producerSession.createProducer(proddest);
	producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	
	Connection listenerConnection = factory.createConnection(apolloUser, apolloPassword);
	listenerConnection.start();
	Session listenerSession = listenerConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	Destination listdest = new StompJmsDestination(topicName);
	final MessageConsumer listener = listenerSession.createConsumer(listdest);
	
	int noOfThreads = 1;
	ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);
	
	//Creating a separate thread to run the listener on the background
	Runnable backgroundTask = new Runnable() {
		@Override
		public void run() {
			while(true) {
			    try {
					Message msg = listener.receive();
					if( msg instanceof  TextMessage ) {
						String body = ((TextMessage) msg).getText();
						System.out.println(body);
						// will split the messsage bofy from colon and save in the collection
						String[] bookinfo = body.split(":");
						Long isbn = Long.parseLong(bookinfo[0]);
						Book book = bookRepository.getBookByISBN(isbn);
						if(book != null){
							book.setStatus(Book.Status.available);
						}
						else
						{
							book = new Book();
							book.setTitle(bookinfo[1]);
							book.setCategory(bookinfo[2]);
							//error correction since it splits at the colon for url request
							book.setCoverimage(new URL(bookinfo[3] + ":" + bookinfo[4]));
							book.setStatus(Book.Status.available);
							Book newbook = bookRepository.saveBook(book);
							newbook.setIsbn(isbn);
						}
					}
				}
			    catch (JMSException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	executor.execute(backgroundTask);

	/** Root API */
	environment.addResource(RootResource.class);
	/** Books APIs */
	environment.addResource(new BookResource(bookRepository,producer,producerSession,libraryName));

	/** UI Resources */
	environment.addResource(new HomeResource(bookRepository));
	
	//executor.shutdown();
	//producerConnection.close();
	//listenerConnection.close();
    }
}
