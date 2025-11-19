package modelAndView;

public class ModelAndView {
    private String viewName;
    private Object data;
    
    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }
    
    public ModelAndView(String viewName, Object data) {
        this.viewName = viewName;
        this.data = data;
    }
    
    public String getViewName() {
        return viewName;
    }
    
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
        

}