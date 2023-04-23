package com.toolslab.task.rest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.ejb.TransactionManagementType;
import javax.ejb.TransactionManagement;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.toolslab.task.model.Calculation;

@RequestScoped
@TransactionManagement(TransactionManagementType.BEAN)
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CalculationService {
    
    @Inject
    private UserTransaction tx;
	
	@PersistenceContext(unitName="calculation-unit")
	private EntityManager em;
	
	
	
	@POST
	@Path("calc")
	public int calculate (Calculation calculation) throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		int result = 0;
		String operation = calculation.getOperation();

		switch (operation) {
		case "+":
			result = calculation.getNumber1() + calculation.getNumber2();
			break;
		case "-":
			result = calculation.getNumber1() - calculation.getNumber2();
			break;
		case "*":
			result = calculation.getNumber1() * calculation.getNumber2();
			break;
		case "/":
			if (calculation.getNumber2() == 0) {
				throw new IllegalArgumentException("Can't Divide By Zero");
			} else {
				result = calculation.getNumber1() / calculation.getNumber2();
			}
			break;
		default:
			throw new IllegalArgumentException("Un Supported Operation");
		}
		
		tx.begin();
	    em.persist(calculation);
        tx.commit();
		return result;
	}
	
	@GET
	@Path("calculations")
	public List<Calculation> getAllCalculations() {
		return em.createQuery("select c from Calculation c", Calculation.class).getResultList();
	}
	
	@GET
	public String serviceStatus() {
		return "Deployed And Running";
	}

}
