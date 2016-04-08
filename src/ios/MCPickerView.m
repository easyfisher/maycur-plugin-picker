#import "MCPickerView.h"

@interface MCPickerView()

@property (nonatomic, strong) UIToolbar *toolbar;
@property (nonatomic, strong) UIButton *okButton;

@end

@implementation MCPickerView

-(instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor whiteColor];
        self.picker = [[UIPickerView alloc] init];
        [self addSubview:self.picker];
        
        self.toolbar = [[UIToolbar alloc] init];
        self.toolbar.barTintColor = [UIColor whiteColor];
        UIBarButtonItem *flexibleSpace = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
        UIBarButtonItem *doneItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneDidTap)];
        self.toolbar.items = @[flexibleSpace, doneItem];
        [self addSubview:self.toolbar];
    }
    return self;
}

- (void)layoutSubviews {
    CGFloat width = self.bounds.size.width;
    CGFloat height = self.bounds.size.height;
    self.toolbar.frame = CGRectMake(0, 0, width, 44);
    self.picker.frame = CGRectMake(0, 24, width, height - 44);
    [self.picker setNeedsLayout];
}

- (void)setDelegate:(id<MCPickerViewDelegate>)delegate {
    self.picker.delegate = delegate;
    self.picker.dataSource = delegate;
    _delegate = delegate;
}

- (void)doneDidTap {
    if ([self.delegate respondsToSelector:@selector(pickerDidClickDone)]) {
        [self.delegate pickerDidClickDone];
    }
}

@end
