package modelAndView;

import java.util.Map;

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
        
    public Map<String, Object> getData() {
        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }
        return null;
    }

    public void setData(Object data) {
        this.data = data;
    }

}