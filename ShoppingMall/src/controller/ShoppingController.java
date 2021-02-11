package controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import model.MemberBean;
import model.ShoppingDAO;
import model.SuBean;
import model.SuCartBean;

public class ShoppingController {

	ShoppingDAO shoppingDao;

	public void setShoppingDao(ShoppingDAO shoppingDao) {
		this.shoppingDao = shoppingDao;
	}

	@RequestMapping("/index.do")
	public ModelAndView index(HttpSession session){
		
		ModelAndView mav = new ModelAndView();

		MemberBean mbean = (MemberBean)session.getAttribute("mbean");
		
		if(mbean==null){
			mav.addObject("mbean",null);
			mav.setViewName("ShoppingMain");
		} else {
			mav.addObject("mbean",mbean);
			mav.setViewName("ShoppingMain");
		}
		
		return mav;
	}
	
	@RequestMapping("/sujak.do")
	public ModelAndView sujak(String num){
		
		ModelAndView mav = new ModelAndView();

		if(num==null){
			List<SuBean> list = shoppingDao.getAllSutool();
			mav.addObject("list",list);
		} else {
			List<SuBean> list = shoppingDao.getSelectSutool(num);
			mav.addObject("list",list);
		}
		
		mav.addObject("center","SujakCenter.jsp");
		mav.addObject("left","SujakLeft.jsp");
		mav.setViewName("ShoppingMain");
		
		return mav;
	}
	
	@RequestMapping("/suinfo.do")
	public ModelAndView suinfo(int suno){
		
		SuBean sbean = shoppingDao.getOneSutool(suno);
		
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("sbean", sbean);
		mav.addObject("center","SujakInfo.jsp");
		mav.addObject("left","SujakLeft.jsp");
		mav.setViewName("ShoppingMain");
		
		return mav;
	}
	
	@RequestMapping("/sutoolcart.do")
	public ModelAndView sutoolCart(SuCartBean cartbean, HttpSession session){
		
		Cart cart = (Cart)session.getAttribute("cart");
		
		if(cart==null){
			cart = new Cart();
			session.setAttribute("cart", cart);
		}
		
		cart.push(cartbean);
		
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("msg", cartbean.getSuname() + "의 상품 " 
							+ cartbean.getSuqty() + "개를 카트에 추가했습니다.");
		mav.addObject("center","SuCartResult.jsp");
		mav.addObject("left","SujakLeft.jsp");
		mav.setViewName("ShoppingMain");
		
		return mav;
	}
	
	@RequestMapping("/sucartdel.do")
	public ModelAndView sucartDel(int suno, HttpSession session){
		
		Cart cart = (Cart)session.getAttribute("cart");
		
		cart.deleteCart(suno);
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("cart",cart);
		mav.addObject("center","SuCartResult.jsp");
		mav.addObject("left","SujakLeft.jsp");
		mav.setViewName("ShoppingMain");
		
		return mav;
	}
}
