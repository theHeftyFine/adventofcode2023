from functools import partial

class Component:
    def __init__(self, value, index, row, id):
        self.value = value
        self.index = index
        self.row = row
        self.id = id
        
    def __str__(self):
        return f"value: {self.value}, index: {self.index}, row: {self.row}"
        
    def len(self):
        return len(self.value)
        
    def numeric(self):
        return int(self.value)
        
class Symbol:
    def __init__(self, symbol, index, row):
        self.symbol = symbol
        self.index = index
        self.row = row
        
    def top(self):
        return self.row - 1 if self.row > 0 else 0
        
    def bottom(self, height):
        return self.row + 1 if self.row < height else height

def calculate_value(matrix, start, row, col, digits, line, lines):
    if digits !='':
        # one column before found number
        start_col = start - 1 if start > 0 else 0
        # one column after found number
        end_col = col + 1 if col + 1 < len(line) - 1 else len(line)
        # one row before found number
        start_row = row - 1 if row > 0 else 0
        # one row after found number
        end_row = row + 1 if row < len(lines) - 1 else len(lines) - 1
        
        block = []
        
        for x in range(start_row, end_row + 1):
            block.extend(matrix[x][start_col:end_col])
            
        
        value = int(digits)
                        
        if any([comp != '.' and not comp.isdigit() for comp in block]):
            # print('found block, adding value', value)
            return value
        else:
            # print('not adding', value)
            return 0;
    
def get_digits(lines):
    components = []
    id = 0
    for row, ln in enumerate(lines):
        line = ln.strip()
        start = 0
        digits = ''
        for col, c in enumerate(line):
            if c.isdigit():
                if digits == '':
                    start = col
                digits = digits + c
                if col == len(line) - 1:
                    components.append(Component(digits, start, row, id))
                    id = id + 1
            else:
                if digits != '':
                    components.append(Component(digits, start, row, id))
                    id = id + 1
                digits = ''
                start = 0
    return components
    
def get_symbols(lines, tokens):
    symbols = []
    for row, ln in enumerate(lines):
        line = ln.strip()
        for col, c in enumerate(line):
            if any([c == token for token in tokens]):
                symbols.append(Symbol(c, col, row))
    return symbols
    
def part_1(lines):
    running_total = 0
    
    matrix = [[c for c in line] for line in lines]

    for row, ln in enumerate(lines):
        line = ln.strip()
        start = 0
        digits = ''
        for col, c in enumerate(line):
            if c.isdigit():
                if digits == '':
                    start = col
                digits = digits + c
                if col == len(line) - 1:
                    running_total = running_total + calculate_value(matrix, start, row, col, digits, line, lines)
            else:
                if digits !='':
                    # print("row", row)
                    running_total = running_total + calculate_value(matrix, start, row, col, digits, line, lines)
                    # print('---------------------------------')
                    # print("total", running_total)
                digits = ''
                start = 0
    return running_total;
    
def filter_digit(top, bottom, left, right, digit):
    within_columns = digit.row >= top and digit.row <= bottom
    head_within_rows = digit.index >= left and digit.index <= right
    tail = digit.index + digit.len() - 1
    tail_within_rows = tail >= left and tail <= right
    return within_columns and (head_within_rows or tail_within_rows)
    
def part_2(lines):
    digits = get_digits(lines)
    symbols = get_symbols(lines, ['*'])
    
    line_len = len(lines[0].strip())
    
    sum = 0
    
    for symbol in symbols:
        top = symbol.top()
        bottom = symbol.bottom(len(lines) - 1)
        left = symbol.index - 1 if symbol.index > 0 else 0
        right = symbol.index + 1 if symbol.index < line_len - 1 else line_len - 1
        
        filter_fun = partial(filter_digit, top, bottom, left, right)
        
        digits_in = [digit for digit in digits if filter_digit(top, bottom, left, right, digit)]
        if len(digits_in) == 2:
            sum = sum + (digits_in[0].numeric() * digits_in[1].numeric())
            
    return sum
    
lines = open('input.txt').readlines()
            
print("solution 1:", part_1(lines));

print("solution 2:", part_2(lines))