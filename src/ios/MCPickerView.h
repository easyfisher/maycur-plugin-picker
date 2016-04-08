#import <UIKit/UIKit.h>

@protocol MCPickerViewDelegate <NSObject, UIPickerViewDelegate, UIPickerViewDataSource>

@optional
- (void)pickerDidClickDone;

@end

@interface MCPickerView : UIView

@property (nonatomic, strong) UIPickerView *picker;
@property (nonatomic, assign) id<MCPickerViewDelegate> delegate;

@end
