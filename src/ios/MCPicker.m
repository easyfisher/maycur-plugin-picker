#import "MCPicker.h"
#import "MCPickerView.h"
#import <Cordova/CDVPlugin.h>

#pragma mark - Picker Node
@interface PickerNode : NSObject

@property (nonatomic, strong) NSString *name;
@property (nonatomic, strong) NSArray<PickerNode *> *childs;

- (instancetype)initWithDictionary:(NSDictionary *)dic;
- (NSInteger)depth;

@end

@implementation PickerNode

- (instancetype)initWithDictionary:(NSDictionary *)dic {
    self = [self init];
    if (self) {
        self.name = [dic objectForKey:@"name"];
        NSArray *array = [dic objectForKey:@"childs"];
        if (array && array.count > 0) {
            NSMutableArray *childs = [NSMutableArray array];
            for (NSDictionary *dic in array) {
                [childs addObject:[[PickerNode alloc] initWithDictionary:dic]];
            }
            self.childs = [NSArray arrayWithArray:childs];
        }
    }
    return self;
}

- (NSInteger)depth {
    if (self.childs && self.childs.count > 0) {
        NSInteger childDepth  = 0;
        for (PickerNode *node in self.childs) {
            if (childDepth < [node  depth]) {
                childDepth = [node depth];
            }
        }
        return childDepth + 1;
    }
    
    return 1;
}

@end


#pragma mark - Picker Implementation
@interface MCPicker() <MCPickerViewDelegate>

@property (nonatomic, strong) CDVInvokedUrlCommand *command;

@property (nonatomic, strong) MCPickerView *pickerView;
@property (nonatomic, strong) UIView *backgroundView;

@property (nonatomic, strong) NSArray<PickerNode *> *nodes;
@property (nonatomic, assign) NSInteger depth;
@property (nonatomic, strong) NSMutableArray<NSArray<PickerNode *> *> *nodesMap;

@end

@implementation MCPicker

- (void)show:(CDVInvokedUrlCommand*)command {
    NSArray *array = command.arguments[0];
    if (array.count <= 0) {
        return;
    }
    
    self.command = command;
    NSMutableArray<PickerNode *> *nodes = [NSMutableArray array];
    for (NSDictionary *dic in array) {
        [nodes addObject:[[PickerNode alloc] initWithDictionary:dic]];
    }
    self.nodes = [NSArray arrayWithArray:nodes];
    self.depth = 0;
    for (PickerNode *node in self.nodes) {
        if (self.depth < [node depth]) {
            self.depth = [node depth];
        }
    }
    self.nodesMap = [NSMutableArray arrayWithCapacity:self.depth];
    
    [self setupView];
}

- (void)setupView {
    UIView *superView = self.webView.superview;
    CGFloat width = superView.bounds.size.width;
    CGFloat height = superView.bounds.size.height;
    
    self.backgroundView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, width, height)];
    self.backgroundView.backgroundColor = [UIColor blackColor];
    self.backgroundView.alpha = 0;
    [superView addSubview:self.backgroundView];
    
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(backgroundDidTap)];
    [self.backgroundView addGestureRecognizer:tapGesture];
    
    self.pickerView = [[MCPickerView alloc] initWithFrame:CGRectMake(0, height, width, 280)];
    self.pickerView.delegate = self;
    [superView addSubview:self.pickerView];
    
    [UIView animateWithDuration:0.35 animations:^() {
        self.backgroundView.alpha = 0.5;
        self.pickerView.frame = CGRectMake(0, height - 280, width, 280);
    }];
}

- (void)backgroundDidTap {
    UIView *superView = self.webView.superview;
    CGFloat width = superView.bounds.size.width;
    CGFloat height = superView.bounds.size.height;
    
    [UIView animateWithDuration:0.35 animations:^() {
        self.backgroundView.alpha = 0;
        self.pickerView.frame = CGRectMake(0, height, width, 280);
    } completion:^(BOOL finished) {
        self.backgroundView = nil;
        self.pickerView = nil;
    }];
}

- (void)pickerDidClickDone {
    NSMutableArray *message = [NSMutableArray array];
    for (int i = 0; i < self.depth; i++) {
        if (self.nodesMap[i].count <= 0)
            break;
        [message addObject:[NSNumber numberWithInteger:[self.pickerView.picker selectedRowInComponent:i]]];
    }
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:message];
    [self.commandDelegate sendPluginResult:result callbackId:self.command.callbackId];
    [self backgroundDidTap];
}

#pragma mark - UIPickerViewDataSource
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView {
    return self.depth;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component {
    NSArray<PickerNode *> *nodes = self.nodes;
    for (int i = 0; i < component; i++) {
        nodes = nodes[[self.pickerView.picker selectedRowInComponent:i]].childs;
    }
    if (!nodes) {
        nodes = [NSArray array];
    }
    self.nodesMap[component] = nodes;
    return nodes.count;
}

#pragma mark - UIPickerViewDelegate
- (UIView *)pickerView:(UIPickerView *)pickerView viewForRow:(NSInteger)row forComponent:(NSInteger)component reusingView:(UIView *)view {
    UILabel *label = [[UILabel alloc] init];
    label.text = self.nodesMap[component][row].name;
    label.adjustsFontSizeToFitWidth = YES;
    label.font = [UIFont systemFontOfSize:22];
    label.minimumScaleFactor = 0.7;
    
    return label;
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component {
    if (component < self.depth - 1) {
        for (NSInteger i = component + 1; i < self.depth; i++) {
            [self.pickerView.picker reloadComponent:i];
            [self.pickerView.picker selectRow:0 inComponent:i animated:YES];
        }
    }
}

@end
