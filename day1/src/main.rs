use std::fs;
use std::str::Lines;

fn main() {
    let bind = fs::read_to_string("input.txt")
		.expect("Should have been able to read the file");
	let contents = bind;
	
	let sum1: u32 = part1(contents.lines()).iter().sum();
	let sum2: u32 = part2(contents.lines()).iter().sum();
		
	println!("part 1: {sum1}");
	println!("part 2: {sum2}");
}

fn part1(contents: Lines) -> Vec<u32> {
	let mut nums: Vec<u32> = Vec::new();
		
	for element in contents {
		let mut v: Vec<u8> = Vec::new();
		for c in element.chars() {
			if let Ok(num) = c.to_string().parse::<u8>() {
				v.push(num);
			}
		}
		if let Some(num) = calculate(&v) {
			nums.push(num);
		}
	}
	return nums;
}
	

fn part2(contents: Lines) -> Vec<u32> {
	let mut nums: Vec<u32> = Vec::new();
		
	for element in contents {
		let mut v: Vec<u8> = Vec::new();
		let mut acc: Vec<char> = Vec::new();		
		for ch in element.chars() {
			acc.push(ch);
			let num = num_or_accum(&acc);
			if num > 0 {
				v.push(num);
			}
		}
		if let Some(num) = calculate(&v) {
			nums.push(num);
		}
	}
	return nums;
}

fn calculate(v: &Vec<u8>) -> Option<u32>{
	if let Some(first) = v.first() {
		if let Some(last) = v.last() {
			let mut digits = first.to_string();
			digits.push_str(&last.to_string());
			if let Ok(sum) = digits.parse::<u32>() {
				return Some(sum);
			}
		}
	}
	return None;
}

fn num_or_accum(acc: &Vec<char>) -> u8 {
	let mut reverse: Vec<&char> = Vec::new();
	
	for c in acc.iter().rev() {
		if let Ok(num) = c.to_string().parse::<u8>() {
			// token is a number
			return num;
		}
		reverse.push(c);
		let word = reverse.iter().cloned().rev().collect::<String>();
		let value: u8 = match <String as AsRef<str>>::as_ref(&word) {
			s if s.contains("one") => 1,
			s if s.contains("two") => 2,
			s if s.contains("three") => 3,
			s if s.contains("four") => 4,
			s if s.contains("five") => 5,
			s if s.contains("six") => 6,
			s if s.contains("seven") => 7,
			s if s.contains("eight") => 8,
			s if s.contains("nine") => 9,
			_ => 10,
		};
		if value < 10 {
			return value;
		}
	}
	return 0;
}
